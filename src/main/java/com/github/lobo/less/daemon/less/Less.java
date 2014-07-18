package com.github.lobo.less.daemon.less;

import java.io.IOException;


public interface Less {

	String compile(String filename) throws IOException;

}
