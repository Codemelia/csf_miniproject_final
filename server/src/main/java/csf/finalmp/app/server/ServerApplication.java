package csf.finalmp.app.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import csf.finalmp.app.server.services.UserService;
import csf.finalmp.app.server.services.MusicianProfileService;
import csf.finalmp.app.server.services.TipService;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

	@Autowired
	private MusicianProfileService musicSvc;

	@Autowired
	private TipService tipSvc;

	@Autowired
	private UserService userSvc;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	// command line runner to create data in mysql
	@Override
	public void run(String... args) throws Exception {

		// check if tables exists, if not create tables
		// order: users > musicians > tips
		boolean usersExists = userSvc.tableExists();
		if(!usersExists) { userSvc.createTable(); }

		boolean musiciansExists = musicSvc.tableExists();
		if (!musiciansExists) { musicSvc.createTable();	}

		boolean tipsExists = tipSvc.tableExists();
		if(!tipsExists) { tipSvc.createTable(); }

	}

}
