#cyeoman-maven-plugin

Fork from https://github.com/trecloux/yeoman-maven-plugin,but with cnpm support for developers!



Use this plugin to integrate the yeoman build into your maven build.
## Prerequisites
* cnpm, bower, grunt and npm-cache(only needed if useNpmCache parameter enabled)
* maven 3

## Launched commands (default setup)
* `cnpm install`
* `bower install --no-color`
* `grunt test --no-color`
* `grunt build --no-color`

## Default layout
Yeoman application is placed into the *yo* directory at the top of the project hierarchy.

	pom.xml
	 - src
	   - main
	     - java
	     - webapp
	     - …
	   - test
	     - ..
	 - yo
	   package.json
	   component.json
	   Gruntfile.js
	   - app
	     index.html
	     ...
	   - test
	     ...
	   - dist
	     ...
## Usage
Plugin declaration :

	<plugin>
   		<groupId>com.github.knownact</groupId>
   	    <artifactId>cyeoman-maven-plugin</artifactId>
   	    <version>0.1</version>
   	    <executions>
   	    	<execution>
   	        	<goals>
   	            	<goal>build</goal>
   	            </goals>
   	        </execution>
   	    </executions>
   	</plugin>

Add the yeoman *dist* directory to the war file

    <plugin>
        <artifactId>maven-war-plugin</artifactId>
        <version>2.3</version>
				<configuration>
            <webResources>
                <resource>
                    <directory>yo/dist</directory>
                </resource>
            </webResources>
        </configuration>
    </plugin>

Configure the clean plugin in order to delete generated directories


    <plugin>
        <artifactId>maven-clean-plugin</artifactId>
        <version>2.5</version>
        <configuration>
            <filesets>
                <fileset>
                    <directory>yo/dist</directory>
                </fileset>
                <fileset>
                    <directory>yo/.tmp</directory>
                </fileset>
                <fileset>
                	<directory>yo/app/components</directory>
                </fileset>
                <fileset>
                  <directory>yo/node_modules</directory>
                </fileset>
            </filesets>
        </configuration>
    </plugin>

## Changelog

### V0.1 2013-04-25
* Initial version

## License
This project is licensed under [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html)
