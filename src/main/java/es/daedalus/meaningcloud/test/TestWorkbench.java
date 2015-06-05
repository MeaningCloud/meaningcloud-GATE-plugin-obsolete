package es.daedalus.meaningcloud.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import es.daedalus.meaningcloud.gate.Post;
import es.daedalus.meaningcloud.gate.clients.ParserClient;
import es.daedalus.meaningcloud.gate.param.TokenBean;

public class TestWorkbench {
	public static void main(String[] args) throws IOException {
		String str = FileUtils.readFileToString(new File("data/marca.txt"));

		Post post;
		post = new Post("http://meaningcloud.com/parser-1.2");
		post.addParameter("key", "");
		post.addParameter("txt", str);
		post.addParameter("lang", "es");
		post.addParameter("of", "json");
		post.addParameter("uw", "n");
		post.addParameter("rt", "y");
		post.addParameter("tt", "a");
		post.addParameter("st", "y");
		post.addParameter("mode", "sa");
		post.addParameter("verbose", "y");

		byte[] response = post.getResponse().getBytes("UTF-8");
		String resp = new String(response, "UTF-8");
		System.out.println(resp);

		ParserClient sc = new ParserClient();
		TokenBean data = sc.getData(resp);
		System.out.println(data.getClass().toString());
		System.out.println(data.status.toString());
		System.out.println(data.toString());
		System.out.println("End");
	}

}
