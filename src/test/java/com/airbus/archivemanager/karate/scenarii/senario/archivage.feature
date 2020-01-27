@Archivage
Feature: Validation of Archivage
  TODO

Background:
* call read('../../common.feature')
* configure headers = getHeaderForUser("user1")
* def archivageTestScenarioName = "Archivage test karate scenario"
##############################################



Scenario: pre-condition a scenario exist
Given url baseUrl + 'scenarios'
And request
"""
{
  "description": "une description",
  "simulation": "2019-11-13T10:01:46.200Z",
  "name": "#(archivageTestScenarioName)",
  "simulationMode": "PLAY",
  "startSimulatedDate": "2019-11-12T10:01:46.200Z"
  }
"""
When method post
Then status 201


Scenario Outline: Create  and delete an archivage <comment>

* deleteFile(ltsPath + "<fileName>")
* createFile(stsPath + "<fileName>")
Given url baseUrl + 'scenarios'
And param name.equals = archivageTestScenarioName
When method get
Then status 200
* def idScenario = response[0].id

Given url baseUrl + 'transfer-archive'
And request
"""
{
  "name": "<name>",
  "outputFiles": [],
  "scenarioFiles": [
    {
      "datasetId": 1,
      "fileType": "oneInputType",
      "format": "oneInputFormat",
      "inputType": "INPUT",
      "relativePathInST": "<fileName>",
      "scenarios": <scenario>,
      "securityLevel": "NORMAL",
      "subSystemAtOriginOfData": "sub system 1",
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

* configure retry = { count: 20, interval: 3000 }
Given url baseUrl + 'transfer-archive/'
And path idArchive
And retry until responseStatus == 200 && ( response.status == 'FAILED' ||  response.status == 'DONE')
When method get
Then status 200
And match response.status == "<status>"

#TODO à finaliser And assert fileExist(ltsPath + "<fileName>") == "<fileArchived>"
Given url baseUrl + 'scenario-files/'
And param relativePathInST.equals = "<fileName>"
When method get
Then status 200
And match response[0].relativePathInST == "<fileName>"


Given url baseUrl + 'transfer-archive/' + idArchive
When method delete
Then status 204

* configure headers = getHeaderForUser("admin")
Given url baseUrl + 'scenario-files/'
And request
"""
{
  "relativePathInST": "<fileName>"
}
"""
When method delete
Then status 204
* deleteFile(ltsPath + "<fileName>")

Examples:
  | name       | status | scenario                 | fileArchived | fileName             | comment                             |
  | transfert1 | DONE   | []                       | True         | F_input_archive1.txt | One file without  scenario          |
  | transfert2 | DONE   | [{"id":"#(idScenario)"}] | True         | F_input_archive2.txt | One file with  scenario             |
#  | transfert3 | FAILED | [{"id":"999999"}]        | False        | F_input_archive3.txt | One file with a  scenario not exist |


#Post Feature to clear every created data

Scenario:  delete Archivage test karate scenario
Given url baseUrl + 'scenarios'
And param name.equals = archivageTestScenarioName
When method get
Then status 200
* def idScenario = response[0].id
Given url baseUrl + 'scenarios/' + idScenario
When method delete
Then status 204
