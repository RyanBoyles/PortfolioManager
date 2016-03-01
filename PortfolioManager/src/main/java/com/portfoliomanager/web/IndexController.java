
package com.portfoliomanager.web;

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
import com.portfoliomanager.internals.UserID;
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
		String newUserCreationStatus = "New user creation failed. Please try again.";

		// TODO: PREVENT DUPLICATE USERS
		
		if (newUser.getVerifiedPassword().equals(newUser.getPassword()))
		{
			User user = new User();
			user.setName(newUser.getName());
			user.getUserID().setEmail(newUser.getEmail());
			user.setPassword(newUser.getPassword());

			userRepo.save(user);

			newUserCreationStatus = "New user creation successful. You can now return to the login screen and log in.";
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
		
		User user = userRepo.findOne( new UserID(validLogin.getEmail()) );
		model.put("user", user);
		
		// TODO: ITERATE THROUGH THE ACCOUNTS OWNED BY THIS USER AND THE STOCKS WITHIN THOSE ACCOUNTS AND POPULATE THE STOCK DATA FOR THIS USER
		
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
		
		// TODO: PREVENT DUPLICATE ACCOUNTS
		
		newAccount.setUser(user);
		user.getAccounts().add(newAccount);
		
		accountRepo.save(newAccount);

		return "user";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}", method = RequestMethod.GET)
	public String accountGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute User user, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		for (Account account : user.getAccounts())
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
		
		// TODO: PREVENT DUPLICATE STOCKS
		
		newStock.getAccounts().add(account);
		account.getStocks().add(newStock);
		
		stockRepo.save(newStock);

		return "account";
	}
	
	@RequestMapping(value = "/sessionkey={sessionKey}/userid={userIDHash}/accountid={accountIDHash}/stockid={stockIDHash}", method = RequestMethod.GET)
	public String stockGet(@PathVariable String sessionKey, @PathVariable int userIDHash, @PathVariable int accountIDHash, @PathVariable int stockIDHash, @ModelAttribute("login") Login validLogin, @ModelAttribute Account account, ModelMap model)
	{
		if ((!sessionKey.equals(this.sessionKey)) || (userIDHash != validLogin.hashCode()))
		{
			return "invalidsessionkey";
		}
		
		for (Stock stock : account.getStocks())
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
	
}
