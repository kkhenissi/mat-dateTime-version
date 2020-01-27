
@MainUseCase
Feature: Main use case  
  Create dummy input and config files in STS
  Create a scenario
  Archive input and config files in this scenario
  Delete these files in STS
  Retrive all input and config file of this scenario
  Create a run
  and 2 times:
    Create output files in STS
    Archive these files for the run
    Delete these files in STS


Background:
* call read('../../common.feature')
* def archivageTestScnenarioName = "main use case test karate scenario"
* configure retry = { count: 10, interval: 1000 }
##############################################


@test1
Scenario: create dummy files and a scenario 
* configure headers = getHeaderForUser("user1")
* createFile(stsPath + "fileInput1.txt")
* createFile(stsPath + "fileConfig1.txt")
Given url baseUrl + 'scenarios'
And request 
"""
{
  "description": "une description", 
  "endSimulatedDate": "2019-11-13T10:01:46.200Z",
  "name": "#(archivageTestScnenarioName)",
  "simalutationMode": "PLAY",
  "startSimulatedDate": "2019-11-12T10:01:46.200Z"
  } 
"""
When method post
Then status 201

@test1
Scenario: Archive Input File
* configure headers = getHeaderForUser("user1")
Given url baseUrl + 'scenarios'
And param name.equals = archivageTestScnenarioName
When method get
Then status 200
* def idScenario = response[0].id

Given url baseUrl + 'transfer-archive'
And request 
"""
{
  "name": "transfertMainUsedCaseInput",
  "outputFiles": [],
  "scenarioFiles": [
    {
      "configDatasetId": null,
      "datasetId": null,
      "fileType": "oneInputType",
      "format": "oneInputFormat",
      "inputType": "INPUT",
      "relativePathInST": "fileInput1.txt",
      "scenarios": [{"id":"#(idScenario)"}],
      "securityLevel": "NORMAL",
      "subSystemAtOriginOfData": "sub system 1",
      "timeOfData": "2019-11-18T10:32:20.127Z"
    },
    
    {
      "configDatasetId": null,
      "datasetId": null,
      "fileType": "oneInputType",
      "format": "oneOutputType",
      "inputType": "CONFIG",
      "relativePathInST": "fileConfig1.txt",
      "scenarios": [{"id":"#(idScenario)"}],
      "securityLevel": "NORMAL",
      "subSystemAtOriginOfData": "sub system 2",
      "timeOfData": "2019-11-18T10:32:20.127Z"
    }
  ]
}
"""
When method post
Then status 201
* print 'response is: ' + response
* def idArchive = response.id
* print 'id ' + idArchive

Given url baseUrl + 'transfer-archive/' 
And path idArchive
And retry until responseStatus == 200 && ( response.status == 'FAILED' ||  response.status == 'DONE')
When method get
Then status 200
And match response.status == "DONE"
* print response.status
#TODO And assert fileExist(ltsPath + "fileInput1.txt") == "True"
#TODO And assert fileExist(ltsPath + "fileConfig1.txt") == "True"

#clean Data
Given url baseUrl + 'transfer-archive/' + idArchive
When method delete
Then status 204



@test1
Scenario: Retrieve Input File
* configure headers = getHeaderForUser("user1")
Given url baseUrl + 'scenarios'
And param name.equals = archivageTestScnenarioName
When method get
Then status 200
* def idScenario = response[0].id


Given url baseUrl + 'scenario-files'
#And param scenariosId.equals = idScenario
When method get
Then status 200
* def listeInputFile = response


Given url baseUrl + 'transfer-retrieve'
And request 
"""
{
  "name": "retrieveMainUsedCaseInput",
  "outputFiles": [],
  "scenarioFiles": #(listeInputFile)
}
"""
When method post
Then status 201
* print 'response is: ' + response
* def idArchive = response.id
* print 'id ' + idArchive

Given url baseUrl + 'transfer-retrieve/' 
* configure headers = getHeaderForUser("user1")
And path idArchive
And retry until responseStatus == 200 && ( response.status == 'FAILED' ||  response.status == 'DONE')
When method get
Then status 200
And match response.status == "DONE"
#TODO And assert fileExist(stsPath + "fileInput1.txt") == "True"
#TODO And assert fileExist(stsPath + "fileConfig1.txt") == "True"

# #clean Data
Given url baseUrl + 'transfer-retrieve/' + idArchive
When method delete
Then status 204

* deleteFile(stsPath + "fileInput1.txt")
* deleteFile(stsPath + "fileConfig1.txt")

Scenario:  Create a Run
* configure headers = getHeaderForUser("user1")
Given url baseUrl + 'scenarios'
And param name.equals = archivageTestScnenarioName
When method get
Then status 200
* def idScenario = response[0].id

Given url baseUrl + 'runs'
And request 
"""
{
  "description": "description of Run",
  "endDate": "2019-12-04T14:29:20.822Z",
  "platformHardwareInfo": "Test Karate",
  "scenarioId": #(idScenario),
  "startDate": "2019-12-03T14:29:20.823Z",
  "status": "IN_PROGRESS",
  "toolVersions": []
}
"""
When method post
Then status 201


Scenario Outline: Create and Archive Output Files <iteration>
* configure headers = getHeaderForUser("user1")
* createFile(stsPath + "fileOutput1_<iteration>.txt")
* createFile(stsPath + "fileOutput2_<iteration>.txt")
Given url baseUrl + 'scenarios'
And param name.equals = archivageTestScnenarioName
When method get
Then status 200
* def idScenario = response[0].id
Given url baseUrl + 'runs'
And param scenarioId.equals = idScenario
And param startDate.equals = "2019-12-03T14:29:20.823Z"
When method get
Then status 200
* def idRun = response[0].id

Given url baseUrl + 'transfer-archive'
And request 
"""
{
  "name": "transfertMainUsedCaseOutput<iteration>",
  "outputFiles": [
    {
      "fileType": "Output1Type",
      "format": "Output1Format",
      "relativePathInST": "fileOutput1_<iteration>.txt",
      "securityLevel": "NORMAL",
      "subSystemAtOriginOfData": "sub system 1",
      "timeOfData": "2019-11-18T10:32:20.127Z",
      "runId":#(idRun)
    },
    {
      "fileType": "Output2Type",
      "format": "Output2Format",
      "relativePathInST": "fileOutput2_<iteration>.txt",
      "securityLevel": "NORMAL",
      "subSystemAtOriginOfData": "sub system 2",
      "timeOfData": "2019-11-18T10:32:20.127Z",
      "runId":#(idRun)
    }
  ],
  "scenarioFiles": []
}
"""
When method post
Then status 201
* print 'response is: ' + response
* def idArchive = response.id
* print 'id ' + idArchive

Given url baseUrl + 'transfer-archive/' 
And path idArchive
And retry until responseStatus == 200 && ( response.status == 'FAILED' ||  response.status == 'DONE')
When method get
Then status 200
And match response.status == "DONE"
* print response
#TODO And assert fileExist(ltsPath + "fileOutput1_<iteration>.txt") == "True"
#TODO And assert fileExist(ltsPath + "fileOutput1_<iteration>.txt") == "True"

#clean Data
Given url baseUrl + 'transfer-archive/' + idArchive
When method delete
Then status 204

* deleteFile(stsPath + "fileOutput1_<iteration>.txt")
* deleteFile(stsPath + "fileOutput2_<iteration>.txt")

Examples:
 | iteration |
 | 1         |
 | 2         |
 | 3         |


#Post Feature to clear evry created data
Scenario:  delete Archivage test karate scenario, run and file
* configure headers = getHeaderForUser("admin")
#Get Scenario archivageTestScnenarioName
Given url baseUrl + 'scenarios'
And param name.equals = archivageTestScnenarioName
When method get
Then status 200
* def idScenario = response[0].id

#Get Runs of this scenario
Given url baseUrl + 'scenario-files'
And param scenariosId.equals = idScenario
When method get
Then status 200
* def idfile1 = response[0].relativePathInST
* def idfile2 = response[1].relativePathInST

Given url baseUrl + 'scenario-files/'
And request {"relativePathInST": "#(idfile1)"}
When method delete
Then status 204

* deleteFile(ltsPath + idfile1)
Given url baseUrl + 'scenario-files/'
And request {"relativePathInST": "#(idfile2)"}
When method delete
Then status 204
* deleteFile(ltsPath + idfile2)

Given url baseUrl + 'runs'
And param scenarioId.equals = idScenario
And param startDate.equals = "2019-12-03T14:29:20.823Z"
When method get
Then status 200
* def idRun = response[0].id

#Get Runs of this scenario
Given url baseUrl + 'output-files'
And param runId.equals = idRun
When method get
Then status 200
* def idfile1 = response[0].relativePathInST
* def idfile2 = response[1].relativePathInST
* def idfile3 = response[2].relativePathInST
* def idfile4 = response[3].relativePathInST
* def idfile5 = response[4].relativePathInST
* def idfile6 = response[5].relativePathInST

Given url baseUrl + 'output-files/'
And request {"relativePathInST": "#(idfile1)"}
When method delete
Then status 204
* deleteFile(ltsPath + idfile1)
Given url baseUrl + 'output-files/'
And request {"relativePathInST": "#(idfile2)"}
When method delete
Then status 204
* deleteFile(ltsPath + idfile2)
Given url baseUrl + 'output-files/'
And request {"relativePathInST": "#(idfile3)"}
When method delete
Then status 204
* deleteFile(ltsPath + idfile3)
Given url baseUrl + 'output-files/'
And request {"relativePathInST": "#(idfile4)"}
When method delete
Then status 204
* deleteFile(ltsPath + idfile4)
Given url baseUrl + 'output-files/'
And request {"relativePathInST": "#(idfile5)"}
When method delete
Then status 204
* deleteFile(ltsPath + idfile5)
Given url baseUrl + 'output-files/'
And request {"relativePathInST": "#(idfile6)"}
When method delete
Then status 204
* deleteFile(ltsPath + idfile6)

Given url baseUrl + 'runs/' + idRun
When method delete
Then status 204

Given url baseUrl + 'scenarios/' + idScenario
When method delete
Then status 204


* deleteFile(ltsPath + "fileInput1.txt")
* deleteFile(ltsPath + "fileConfig1.txt")