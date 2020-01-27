@ignore
Feature:



#Background:
* headerAdminData headerAdminData
* headerUser1Data headerUser1Data
* headerUser2Data headerUser2Data


Scenario:
  * def getHeaderForUser =
"""
   function(userLogin){
       switch (userLogin) {

        case 'admin':
            return headerAdminData;
            break;
        case 'user1':
            return headerUser1Data;
            break;
        case 'user2':
            return headerUser2Data;
            break;
        }
        return ""
    }
"""
  * def fileExist =
  """
    function(fileName){

    var JavaUtil = Java.type('com.airbus.archivemanager.karate.scenarii.KarateUtil');
    var javaUtil = new JavaUtil();
    return javaUtil.fileExist(fileName);
   }
"""

  * def deleteFile =
"""
    function(fileName){

    var JavaUtil = Java.type('com.airbus.archivemanager.karate.scenarii.KarateUtil');
    var javaUtil = new JavaUtil();
    return javaUtil.deleteFile(fileName);
    }
"""

  * def createFile =
"""
    function(fileName){

    var JavaUtil = Java.type('com.airbus.archivemanager.karate.scenarii.KarateUtil');
    var javaUtil = new JavaUtil();
    return javaUtil.createFile(fileName);
    }
"""


  * def createFiles =
"""
    function(path, fileName, nb){

    var JavaUtil = Java.type('com.airbus.archivemanager.karate.scenarii.KarateUtil');
    var javaUtil = new JavaUtil();
    return javaUtil.createFiles(path, fileName, nb);
    }
"""

  * def world = function(){ return 'world' }
