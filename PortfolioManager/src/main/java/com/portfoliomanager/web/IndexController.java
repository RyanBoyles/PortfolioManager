
package com.portfoliomanager.web;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import com.portfoliomanager.internals.NumberOfShares;
import com.portfoliomanager.internals.Scraper;
import com.portfoliomanager.internals.SimilarStockFinder;
import com.portfoliomanager.internals.StockInfo;
import com.portfoliomanager.repository.AccountRepository;
import com.portfoliomanager.repository.StockRepository;
import com.portfoliomanager.repository.UserRepository;

@Controller
@SessionAttributes({"login", "sessionKey", "userRepo", "accountRepo", "stockRepo", "user", "account", "stock"})
public class IndexController
{
	private String sessionKey;
	
	@Autowired
	private UserRepository userRepo;

	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private StockRepository stockRepo;
	
	private Date lastUpdateTime;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String homeGet(ModelMap model)
	{
		model.put("userRepo", userRepo);
		model.put("accountRepo", accountRepo);
		model.put("stockRepo", stockRepo);
		
		return "home";
	}

	@RequestMapping(value = "/", method = RequestMethod.POST)
	public String homePost(ModelMap model)
	{
		model.put("userRepo", userRepo);
		model.put("accountRepo", accountRepo);
		model.put("stockRepo", stockRepo);
		
		return "home";
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logoutGet(ModelMap model)
	{
		sessionKey = "";

		return "redirect:/";
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
		model.put("newUser", newUser);
		
		String newUserCreationStatus;
		
		if (newUser.getVerifiedPassword().equals(newUser.getPassword()))
		{
			if (!newUser.getPassword().equals(""))
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
				newUserCreationStatus = "New user creation failed. Password cannot be blank. Please try again.";
			}
		}
		else
		{
			newUserCreationStatus = "New user creation failed. Passwords do not match. Please try again.";
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
		
		Date currentTime = new Date();
		long minutesSinceLastUpdate = 31L; // If there is an error with finding the time since the last update, we want to make sure if updates. (i.e. time since update > 30)
		
		if (lastUpdateTime != null)
		{
			minutesSinceLastUpdate = TimeUnit.MINUTES.convert(currentTime.getTime() - lastUpdateTime.getTime(), TimeUnit.MILLISECONDS);
		}
		
		if ((lastUpdateTime == null) || (minutesSinceLastUpdate > 30))
		{
			List<Stock> allStocks = stockRepo.findAll();
			for (Stock stock : allStocks)
			{	
				StockInfo info = Scraper.scrapeAll(stock.getStockID().getSymbol(), stock.getStockID().getExchange());
			
				stockRepo.updateStock(stock.getStockID().getSymbol(), stock.getStockID().getExchange(), info.name, info.price, info.priceChange, info.perChange, info.open, info.todayHigh, info.todayLow, info.weekHigh, info.weekLow, info.pe, info.yield);
			
				/*
				System.out.println("Symbol = " + stock.getStockID().getSymbol());
				System.out.println("Exchange = " + stock.getStockID().getExchange());
				System.out.println("Price = " + info.price);
				System.out.println("PriceChange = " + info.priceChange);
				System.out.println("PercentChange = " + info.perChange);
				System.out.println();
			 	*/
			}
			
			lastUpdateTime = new Date();
		}
		
		return "user";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}", method = RequestMethod.POST)
	public String userPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		return "redirect:/sessionkey=" + sessionKey + "/userid=" + userIDHash;
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
		
		if (editedUser.getVerifiedPassword().equals(editedUser.getPassword()))
		{
			user.setName(editedUser.getName());
			if (!editedUser.getPassword().equals(""))
			{
				user.setPassword(editedUser.getPassword());
			}
			
			userRepo.updateUser(user.getUserID().getEmail(), user.getName(), user.getPassword());
			
			validLogin.setPassword(user.getPassword());
			
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

		return "deleteuser";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/deleteuser", method = RequestMethod.POST)
	public String deleteUserPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		List<Account> accounts = accountRepo.findAccountsByUser(user.getUserID().getEmail());
		for (Account account : accounts)
		{
			accountRepo.removeAllStocksHeldInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber());
		}
		//stockRepo.removeStocksHeldInNoAccounts();
		accountRepo.removeAccountsOwnedByUser(user.getUserID().getEmail());
		userRepo.removeUser(user.getUserID().getEmail());

		return "redirect:/";
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
		
		List<Account> accounts = accountRepo.findAccountsByUser(user.getUserID().getEmail());
		for (Account account : accounts)
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
		
		return "deleteaccount";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/deleteaccount", method = RequestMethod.POST)
	public String deleteAccountPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, @ModelAttribute Account account, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		accountRepo.removeAllStocksHeldInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber());
		accountRepo.removeAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber());
		
		return "redirect:/sessionkey=" + sessionKey + "/userid=" + userIDHash;
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
		
		List<Stock> duplicateStockInAccount = stockRepo.findStockInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber(), newStock.getStockID().getExchange(), newStock.getStockID().getSymbol());
		if (duplicateStockInAccount.isEmpty())
		{
			newStock.getAccounts().add(account);
			account.getStocks().add(newStock);
		
			List<Stock> duplicateStockInStockTable = stockRepo.findStock(newStock.getStockID().getExchange(), newStock.getStockID().getSymbol());
			if (duplicateStockInStockTable.isEmpty())
			{
				stockRepo.addStock(newStock.getStockID().getExchange(), newStock.getStockID().getSymbol());
				
				StockInfo info = Scraper.scrapeAll(newStock.getStockID().getSymbol(), newStock.getStockID().getExchange());
				
				stockRepo.updateStock(newStock.getStockID().getSymbol(), newStock.getStockID().getExchange(), info.name, info.price, info.priceChange, info.perChange, info.open, info.todayHigh, info.todayLow, info.weekHigh, info.weekLow, info.pe, info.yield);
			}
			accountRepo.addStockToAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber(), newStock.getStockID().getExchange(), newStock.getStockID().getSymbol());
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
		
		List<Stock> stocks = stockRepo.findAllStocksInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber());
		for (Stock stock : stocks)
		{
			if (stock.getStockID().hashCode() == stockIDHash)
			{
				model.put("stock", stock);
				break;
			}
		}
		
		SimilarStockFinder similarStockFinder = new SimilarStockFinder();
		model.put("similarStockFinder", similarStockFinder);
		
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
		
		NumberOfShares editedNumberOfShares = new NumberOfShares();
		Long numberOfShares = accountRepo.findSharesByAccountStock(account.getAccountID().getCompany(), account.getAccountID().getNumber(), stock.getStockID().getExchange(), stock.getStockID().getSymbol());
		editedNumberOfShares.setValue(numberOfShares);
		
		model.put("editedNumberOfShares", editedNumberOfShares);
		
		return "editstock";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}/editstock", method = RequestMethod.POST)
	public String editStockPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, @ModelAttribute Stock stock, @ModelAttribute NumberOfShares editedNumberOfShares, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		model.put("editedNumberOfShares", editedNumberOfShares);
		
		String editStockStatus;
		
		if (editedNumberOfShares.getValue() >= 0)
		{
			accountRepo.updateSharesOfAccountStock(account.getAccountID().getCompany(), account.getAccountID().getNumber(), stock.getStockID().getExchange(), stock.getStockID().getSymbol(), editedNumberOfShares.getValue());
		
			editStockStatus = "Stock information updated successfully.";
		}
		else
		{
			editStockStatus = "Stock information update failed. You cannot have a negative number of shares. Please try again.";
		}
		
		model.put("editStockStatus", editStockStatus);
		
		return "editstock";
	}
		
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}/deletestock", method = RequestMethod.GET)
	public String deleteStockGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, @ModelAttribute Stock stock, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		return "deletestock";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}/deletestock", method = RequestMethod.POST)
	public String deleteStockPost(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, @ModelAttribute Stock stock, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		accountRepo.removeStockHeldInAccount(account.getAccountID().getCompany(), account.getAccountID().getNumber(), stock.getStockID().getExchange(), stock.getStockID().getSymbol());
		//stockRepo.removeStocksHeldInNoAccounts();
		
		return "redirect:/sessionkey=" + sessionKey + "/userid=" + userIDHash + "/accountid=" + accountIDHash;
	}

}
