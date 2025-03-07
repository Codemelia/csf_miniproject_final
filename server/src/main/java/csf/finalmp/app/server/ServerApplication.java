package csf.finalmp.app.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import csf.finalmp.app.server.services.MusicianService;
import csf.finalmp.app.server.services.TipService;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

	@Autowired
	private MusicianService musicSvc;

	@Autowired
	private TipService tipSvc;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	// command line runner to create data in mysql
	@Override
	public void run(String... args) throws Exception {

		// check if musicians table exists, if not create table
		boolean musiciansExists = musicSvc.tableExists();
		if (!musiciansExists) {
			musicSvc.createTable();
		}

		// check if tips table exists, if not create table
		boolean tipsExists = tipSvc.tableExists();
		if(!tipsExists) {
			tipSvc.createTable();
		}

	}

}
