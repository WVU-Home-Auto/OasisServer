package edu.wvu.solar.testplugin;

import org.json.JSONArray;

import edu.wvu.solar.oasisserver.plugins.PluginManager;
import edu.wvu.solar.oasisserver.plugins.api.Plugin;

/**
 * 
 * @author Timothy Scott
 * 
 * This test plugin serves as a template for setting up plugin projects.
 * This is currently an annoyingly tedious process, but it will hopefully be
 * made easier in the future.
 * 
 *  1. Install oasisserver.jar in your computer's local Maven repository:
 *    a. Make sure the oasisserver project shows up in Eclipse
 *    b. Right click on oasisserver > Run as > Maven build...
 *    c. In "goals" type "clean install"
 *    d. Click "Apply", then "Run"
 *  2. Create the plugin project
 *    a. Click File > New > Other... > Maven Project
 *    b. Set up the project however you want.
 *  3. Add oasisserver as a dependency
 *    a. In the pom.xml of the newly created project, add the following
 *    	 lines within the project tag:
    <dependencies>
		<dependency>
			<groupId>edu.wvu.solar</groupId>
			<artifactId>oasisserver</artifactId>
			<version>1.0</version>
		</dependency>
	</dependencies>
	
 *  4. Create your Plugin class. This class should extend 
 *     edu.wvu.solar.oasisserver.plugins.api.Plugin
 *  5. Set up the newly created project to build as a jar
 *    a. In the pom.xml, add the following within the project tag:
 *    
     <build>
		<plugins>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-jar-plugin</artifactId>
				<version>3.0.2</version>
				<configuration>
					<archive>
						<index>true</index>
						<manifest>
							<addClasspath>true</addClasspath>
						</manifest>
						<manifestEntries>
							<mode>development</mode>
							<url>${project.url}</url>
							<PluginClass>[NAME OF PLUGIN CLASS]</PluginClass>
							<PluginName>[NAME OF PLUGIN]</PluginName>
						</manifestEntries>
					</archive>
				</configuration>
			</plugin>
		</plugins>
	</build>
 *   b. [NAME OF PLUGIN CLASS] should be replaced with the fully-qualified name
 *      of the Plugin subclass you created in Step 4. "Fully-qualified" means it
 *      includes the package name.
 *   c. [NAME OF PLUGIN] should be replaced with the name of your plugin. This name
 *      should be meaningful and unique.
 * 6. Build your plugin
 *   a. Right click the plugin project in Eclipse and select Run as > Maven Build
 *   b. In "Goals", type "clean install"
 *   c. The newly created jar will be in the target directory of your project
 *   d. Congratulations, you now have a new plugin!
 */
public class TestPlugin extends Plugin{

	public TestPlugin(){
		super("WOW! SUCH TEST!");
	}
	
	public TestPlugin(String plugName) {
		super(plugName);
	}

	@Override
	public void initialize(PluginManager p, JSONArray j) {
		
	}

	@Override
	public String getName(){
		return "WOW, very much test wow yes wow";
	}
	
}
