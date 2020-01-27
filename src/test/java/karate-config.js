function fn() {   
    var env = karate.env; // get java system property 'karate.env'
    karate.log('karate.env system property was:', env);
    if (!env) {
      env = 'dev'; // a custom 'intelligent' default
    }
    var config = { // base config JSON
      baseUrl: 'http://localhost:8080/api/',
      stsPath: 'C:\\temp\\STS\\',
      ltsPath: 'C:\\temp\\LTS\\',
      adminPasswd: "admin",
    };
    if (env == 'ci') {
      config.baseUrl = 'http://localhost:8080/api/';
      config.stsPath = '/tmp/STS/';
      config.ltsPath ='/tmp/LTS/';
      config.adminPasswd = "admin"
    } else if (env == 'val') {
      karate.configure('ssl', true)
      config.baseUrl = 'https://and-ads-am-02.akka.eu/api/';
      config.stsPath = '/data2/STS/';
      config.ltsPath ='/data1/LTS/';
      config.adminPasswd = "admin"
    } else if (env == 'derisk') {
      karate.configure('ssl', true)
      config.baseUrl = 'https://derisk/api/';
      config.stsPath = '/home/STS/';
      config.ltsPath = '/home/LTS/';
      config.adminPasswd = "admin";
    }
    var password = karate.properties['archvMgn.admin.password'] ;
    if (password) {
      config.adminPasswd = password;
    }
    var baseUrl = karate.properties['archvMgn.baseUrl'] ;
    if (baseUrl) {
      config.baseUrl = baseUrl+'api/';
    }

    // create user1 and user2 with admin user
	  var configToken = { login: "admin", password: config.adminPasswd, baseUrl: config.baseUrl};
	  var result = karate.callSingle('classpath:com/airbus/archivemanager/karate/GetAdminBearer.feature',configToken);
	  config.headerAdminData = result.headerData;
    config.authheader = { 'Authorization' :  "Bearer " + config.token}    
	  configToken = { headerAdminData: config.headerAdminData, baseUrl: config.baseUrl};
    karate.callSingle('classpath:com/airbus/archivemanager/karate/CreateUsers.feature',configToken);
    
    //get user1 bearer token 
	  configToken = { login: "user1", password: "user1", baseUrl: config.baseUrl};
	  result = karate.callSingle('classpath:com/airbus/archivemanager/karate/GetAdminBearer.feature',configToken);
    config.headerUser1Data = result.headerData;
    
    //get user2 bearer token 
	  configToken = { login: "user2", password: "user2", baseUrl: config.baseUrl};
	  result = karate.callSingle('classpath:com/airbus/archivemanager/karate/GetAdminBearer.feature',configToken);
	  config.headerUser2Data = result.headerData;

    
    // don't waste time waiting for a connection or if servers don't respond within 5 seconds
    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);
    karate.log('karate.env =', karate.env);
    karate.log('config.baseUrl=', config.baseUrl);
    return config;
  }