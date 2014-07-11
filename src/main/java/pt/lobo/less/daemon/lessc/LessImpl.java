package pt.lobo.less.daemon.lessc;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.lesscss.deps.org.apache.commons.io.IOUtils;

import pt.lobo.less.daemon.Less;

import com.google.common.collect.Lists;
import com.google.inject.Singleton;

@Singleton
public class LessImpl implements Less {
	
	private static final String LESSC_PATH = "/usr/local/bin/lessc";
	
	private static final String LESSC_OPTIONS[] = {
		"--compress"
	};
	
	public String compile() throws IOException, InterruptedException {
		List<String> command = Lists.newArrayList();
		command.add(LESSC_PATH);
		for(String option : LESSC_OPTIONS)
			command.add(option);
		command.add("/Users/dev/git/egoi/megan-web-ui/resources/assets/less/default.less");
		ProcessBuilder builder = new ProcessBuilder(command);
		final Process process = builder.start();
		
		final StringBuilder output = new StringBuilder();
		InputStream stream = process.getInputStream();
		String css = IOUtils.toString(stream);
		output.append(css);

		new Thread(new Runnable() {
			public void run() {
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}).start();
		
		/*
		new Thread(new Runnable() {
			public void run() {
				StringBuilder error = new StringBuilder();
				try {
					InputStream stream = process.getErrorStream();
					int b;
					while((b = stream.read()) != 0)
						error.append((char)b);
					throw new RuntimeException(error.toString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		*/
		
		int result = process.waitFor();
		System.out.println(output.toString());
		
		return output.toString();
	}
	
}
