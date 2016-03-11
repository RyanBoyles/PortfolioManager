
package com.portfoliomanager.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.portfoliomanager.domain.Account;
import com.portfoliomanager.domain.Stock;
import com.portfoliomanager.domain.User;
import com.portfoliomanager.internals.Login;
import com.portfoliomanager.internals.NewUser;
import com.portfoliomanager.repository.AccountRepository;
import com.portfoliomanager.repository.StockRepository;
import com.portfoliomanager.repository.UserRepository;

@Controller
@SessionAttributes({"login", "sessionKey", "user", "account", "stock"})
public class IndexController
{
	private String sessionKey;
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private StockRepository stockRepo;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String homeGet(ModelMap model)
	{
		return "home";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String homePost(ModelMap model)
	{
		return "home";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutGet(ModelMap model)
	{
		sessionKey = "";

		return "home";
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String loginGet(ModelMap model)
	{
		Login login = new Login();

		model.put("login", login);

		return "login";
	}

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String loginPost(@ModelAttribute Login login, ModelMap model)
	{
		login.checkIfValid(userRepo);
		
		if (!login.isValid())
		{
			login.setEmail("");
			login.setPassword("");
			
			model.put("errorMessage", "Login invalid.");
		}
		
		return "login";
	}
	
	@RequestMapping(value = "/login/newuser", method = RequestMethod.GET)
	public String newUserGet(ModelMap model)
	{
		NewUser newUser = new NewUser();

		model.put("newUser", newUser);

		return "newuser";
	}

	@RequestMapping(value = "/login/newuser", method = RequestMethod.POST)
	public String newUserPost(@ModelAttribute NewUser newUser, ModelMap model)
	{
		String newUserCreationStatus;
		
		if (newUser.getPassword().equals(""))
		{
			if (newUser.getVerifiedPassword().equals(newUser.getPassword()))
			{
				List<User> duplicateUser = userRepo.findUserByEmail( newUser.getEmail() );
				if (duplicateUser.isEmpty())
				{			
					User user = new User();
					user.setName(newUser.getName());
					user.getUserID().setEmail(newUser.getEmail());
					user.setPassword(newUser.getPassword());

					userRepo.addUser(user.getUserID().getEmail(), user.getName(), user.getPassword());

					newUserCreationStatus = "New user creation successful. You can now return to the login screen and log in.";
				}
				else
				{
					newUserCreationStatus = "New user creation failed. There is already an account associated with the given email.";
				}
			}
			else
			{
				newUserCreationStatus = "New user creation failed. Passwords do not match. Please try again.";
			}
		}
		else
		{
			newUserCreationStatus = "New user creation failed. Password cannot be blank. Please try again.";
		}

		model.put("newUserCreationStatus", newUserCreationStatus);

		return "newuser";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}", method = RequestMethod.GET)
	public String userGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, ModelMap model)
	{
		this.sessionKey = validLogin.getSessionKey();
		model.put("sessionKey", this.sessionKey);
		
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		User user = userRepo.findUserByEmail( validLogin.getEmail() ).get(0);
		model.put("user", user);
		
		// TODO: ITERATE THROUGH THE ACCOUNTS OWNED BY THIS USER AND THE STOCKS WITHIN THOSE ACCOUNTS AND POPULATE THE STOCK DATA FOR THIS USER. NOTE THE CRAWLER NEEDS TO AVERAGE (OR GET THE MOST RECENT) INFO FROM MULTIPLE SITES TO COUNT AS AN ADVANCED FUNCTION.
		
		
		return "user";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}", method = RequestMethod.POST)
	public String userPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		return "user";
	}

	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/edituser", method = RequestMethod.GET)
	public String editUserGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		NewUser editedUser = new NewUser(user);
		model.put("editedUser", editedUser);

		return "edituser";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/edituser", method = RequestMethod.POST)
	public String editUserPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, @ModelAttribute NewUser editedUser, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		model.put("editedUser", editedUser);
		
		String editUserStatus = "";
		
		if (editedUser.getVerifiedPassword().equals(editedUser.getPassword()))		// TODO: NAME STILL CHANGES EVEN IF PASSWORDS DON'T MATCH, BUT THE STATUS MESSAGE SAYS "PASSWORDS DO NOT MATCH". HOW DOES THAT HAPPEN?
		{
			user.setName(editedUser.getName());
			if (!editedUser.getPassword().equals(""))
			{
				user.setPassword(editedUser.getPassword());
			}
			
			userRepo.updateUser(user.getUserID().getEmail(), editedUser.getName(), editedUser.getPassword());
			
			validLogin.setPassword(editedUser.getPassword());
			
			editUserStatus = "User information updated successfully.";
		}
		else
		{
			editUserStatus = "Failed to update user information. Passwords do not match. Please try again.";
		}
		
		model.put("editUserStatus", editUserStatus);

		return "edituser";
	}

	
	
	
	
	
	
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/deleteuser", method = RequestMethod.GET)
	public String deleteUserGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		// TODO: ADD A NEW LOGIN OBJECT TO THE MODEL AND MAKE THEM TYPE THEIR PASSWORD AND CHECK IF IT MATCHES THE VALID LOGIN INFO?

		return "deleteuser";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/deleteuser", method = RequestMethod.POST)
	public String deleteUserPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		// TODO: ACTUALLY DELETE THE USER, INCLUDING ALL OF ITS ACCOUNTS. DOES THE FOLLOWING SQL DO THAT?
		for (Account account : user.getAccounts())
		{
			accountRepo.removeAllStocksHeldInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber());
		}
		accountRepo.removeAccountsOwnedByUser(user.getUserID().getEmail());
		userRepo.removeUser(user.getUserID().getEmail());

		return "home";
	}
	
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/newaccount", method = RequestMethod.GET)
	public String newAccountGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		Account newAccount = new Account();

		model.put("account", newAccount);

		return "newaccount";
	}

	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/newaccount", method = RequestMethod.POST)
	public String newAccountPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, @ModelAttribute("account") Account newAccount, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		List<Account> duplicateAccount = accountRepo.findAccountByUserCompanyNumber(user.getUserID().getEmail(), newAccount.getAccountID().getCompany(), newAccount.getAccountID().getNumber());
		if (duplicateAccount.isEmpty())
		{
			newAccount.setUser(user);
			user.getAccounts().add(newAccount);
		
			accountRepo.addAccountToUser(user.getUserID().getEmail(), newAccount.getLabel(), newAccount.getAccountID().getCompany(), newAccount.getAccountID().getNumber());
		}

		return "user";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}", method = RequestMethod.GET)
	public String accountGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		for (Account account : user.getAccounts()) // TODO: CONVERT THIS TO NATIVE SQL? MAYBE JUST CONVERT THE GETACCOUNTS FUNCTION TO NATIVE SQL AND CALL IT GOOD?
		{
			if (account.getAccountID().hashCode() == accountIDHash)
			{
				model.put("account", account);
				break;
			}
		}
		
		return "account";
	}

	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}", method = RequestMethod.POST)
	public String accountPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		return "account";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/editaccount", method = RequestMethod.GET)
	public String editAccountGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, @ModelAttribute Account account, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		Account editedAccount = new Account(account);
		model.put("editedAccount", editedAccount);
		
		return "editaccount";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/editaccount", method = RequestMethod.POST)
	public String editAccountPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, @ModelAttribute Account account, @ModelAttribute Account editedAccount, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		model.put("editedAccount", editedAccount);
		
		account.setLabel(editedAccount.getLabel());
		
		accountRepo.updateAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber(), account.getLabel());
		
		String editAccountStatus = "Account information updated successfully.";
		model.put("editAccountStatus", editAccountStatus);
		
		return "editaccount";
	}
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/deleteaccount", method = RequestMethod.GET)
	public String deleteAccountGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, @ModelAttribute Account account, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		// TODO: ADD A NEW LOGIN OBJECT TO THE MODEL AND MAKE THEM TYPE THEIR PASSWORD AND CHECK IF IT MATCHES THE VALID LOGIN INFO?
		
		return "deleteaccount";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/deleteaccount", method = RequestMethod.POST)
	public String deleteAccountPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, @ModelAttribute Account account, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		// TODO: ACTUALLY DELETE THE ACCOUNT, INCLUDING ALL OF ITS STOCKS (ONLY IF THEY AREN'T REFERENCED BY ANOTHER ACCOUNT) DOES THE FOLLOWING SQL WORK?
		accountRepo.removeAllStocksHeldInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber());
		accountRepo.removeAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber());
		
		return "user";
	}
	
	
	
	
	
	
	
	
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/newstock", method = RequestMethod.GET)
	public String newStockGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		Stock newStock = new Stock();

		model.put("stock", newStock);

		return "newstock";
	}

	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/newstock", method = RequestMethod.POST)
	public String newStockPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, @ModelAttribute("stock") Stock newStock, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		// TODO: PREVENT DUPLICATE STOCKS. DOES THE FOLLOWING SQL WORK?
		List<Stock> duplicateStock = stockRepo.findStockInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber(), newStock.getStockID().getExchange(), newStock.getStockID().getSymbol());
		if (duplicateStock.isEmpty())
		{
			newStock.getAccounts().add(account);
			account.getStocks().add(newStock);
		
			//stockRepo.save(newStock); // TODO: CONVERT THIS TO NATIVE SQL. DOES THE FOLLOWING SQL WORK?
			stockRepo.addStock(newStock.getStockID().getExchange(), newStock.getStockID().getSymbol());
			accountRepo.addStockToAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber(), newStock.getStockID().getExchange(), newStock.getStockID().getSymbol());
		
			// TODO: UPDATE THIS STOCKS REAL TIME INFORMATION (FIRST CHECK IF THE STOCK RESIDES IN ANY OTHER ACCOUNTS OWNED BY THE USER. IF IT DOES, JUST TAKE THAT INFORMATION)
		
		}

		return "account";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}", method = RequestMethod.GET)
	public String stockGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		for (Stock stock : account.getStocks()) // TODO: CONVERT THIS TO NATIVE SQL? MAYBE JUST CONVERT THE GETSTOCKS FUNCTION TO NATIVE SQL AND CALL IT GOOD?
		{
			if (stock.getStockID().hashCode() == stockIDHash)
			{
				model.put("stock", stock);
				break;
			}
		}
		
		return "stock";
	}

	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}", method = RequestMethod.POST)
	public String stockPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		return "stock";
	}
	
	
	
	
	
	
	
	
	

	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}/editstock", method = RequestMethod.GET)
	public String editStockGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, @ModelAttribute Stock stock, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		//Stock editedStock = new Stock(stock);
		//model.put("editedStock", editedStock);
		
		Long editedNumberOfShares = accountRepo.findSharesByAccountStock(account.getAccountID().getCompany(), account.getAccountID().getNumber(), stock.getStockID().getExchange(), stock.getStockID().getSymbol());
		model.put("editedNumberOfShares", editedNumberOfShares);
		
		return "editstock"; // TODO: MAKE AN EDITSTOCK.HTML FILE
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}/editstock", method = RequestMethod.POST)
	public String editStockPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, @ModelAttribute Stock stock, @ModelAttribute Long editedNumberOfShares, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		//model.put("editedStock", editedStock);
		
		model.put("editedNumberOfShares", editedNumberOfShares);
		
		String editStockStatus;
		
		if (editedNumberOfShares >= 0)
		{
			//stockRepo.save(stock); // TODO: CONVERT THIS TO NATIVE SQL. DOES THE FOLLOWING SQL WORK?
			accountRepo.updateSharesOfAccountStock(account.getAccountID().getCompany(), account.getAccountID().getNumber(), stock.getStockID().getExchange(), stock.getStockID().getSymbol(), editedNumberOfShares);
		
			editStockStatus = "Stock information updated successfully.";
		}
		else
		{
			editStockStatus = "Stock information update failed. You cannot have a negative number of shares. Please try again.";
		}
		
		model.put("editStockStatus", editStockStatus);
		
		return "editstock"; // TODO: MAKE AN EDITSTOCK.HTML FILE
	}
		
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}/deletestock", method = RequestMethod.GET)
	public String deleteStockGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, @ModelAttribute Stock stock, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		// TODO: ADD A NEW LOGIN OBJECT TO THE MODEL AND MAKE THEM TYPE THEIR PASSWORD AND CHECK IF IT MATCHES THE VALID LOGIN INFO?
		
		return "deletestock";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}/deletestock", method = RequestMethod.POST)
	public String stockGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, @ModelAttribute Stock stock, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		// ACTUALLY REMOVE THE STOCK FROM THE ACCOUNT AND REMOVE THE STOCK FROM THE STOCK TABLE IF IT IS NO LONGER IN ANY ACCOUNT. DOES THE FOLLOWING SQL WORK?
		accountRepo.removeStockHeldInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber(), stock.getStockID().getExchange(), stock.getStockID().getSymbol());
		
		return "account";
	}
	
	
	
	
	
	
	
	
}
