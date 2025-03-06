package csf.finalmp.app.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import csf.finalmp.app.server.services.MusicianService;

@SpringBootApplication
public class ServerApplication implements CommandLineRunner {

	@Autowired
	private MusicianService musicSvc;

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

	// command line runner to create musicians table in mysql
	@Override
	public void run(String... args) throws Exception {
		boolean musiciansExists = musicSvc.tableExists(); // check if table exists
		if (!musiciansExists) {
			musicSvc.createTable(); // create table if it doesn't exist
		}
	}

}
