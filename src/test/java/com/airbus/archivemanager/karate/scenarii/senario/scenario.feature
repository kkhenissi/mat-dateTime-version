Feature: Valivation of Creation Scenario
  Create a scenario with user1,
  Modify this scenario with user1,
  Modification by user2 forbidden
  Delete scenario

#Background:
* url baseUrl
##############################################

@ignore
@Scenario
Scenario: Create modify and delete a Scenario

* call read('../../common.feature')
Given url baseUrl + 'authenticate'
And request { "username": 'admin',	"rememberMe": 'true', "password": 'admin'}
When method post
Then status 200
* print 'response is: ' + response
* def jwt = get response.id_token
* print 'jwt: ' + jwt
* def headerAdminData = { Authorization: #('Bearer ' + jwt), Accept: 'application/problem+json'}
* print headerAdminData
#* headers headerData

* configure headers = getHeaderForUser("user1")
Given url baseUrl + 'scenarios'
And request
"""
{
  "description": "une description",
  "simulation": "2019-11-13T10:01:46.200Z",
  "name": "First scnénario",
  "simulationMode": "PLAY",
  "startSimulatedDate": "2019-11-12T10:01:46.200Z"
  }
"""
When method post
Then status 201
* print 'response is: ' + response
* def idScenario = response.id
* print 'id ' + idScenario


* configure headers = getHeaderForUser("user1")
Given url baseUrl + 'scenarios'
And request
"""
{
  "id" : #(id),
  "description": "une description diff",
  "simulation": "2019-11-14T10:01:46.200Z",
  "name": "Second name scnénario",
  "simulationMode": "PLAY",
  "startSimulatedDate": "2019-11-13T10:01:46.200Z"
  }
"""
When method put
Then status 200
* print 'response is: ' + response
* def idScenario = response.id
* print 'id ' + idScenario


* configure headers = getHeaderForUser("user2")
Given url baseUrl + 'scenarios'
And request
"""
{
  "description": "une description",
  "simulation": "2019-11-13T10:01:46.200Z",
  "name": "First scnénario",
  "simulationMode": "PLAY",
  "startSimulatedDate": "2019-11-12T10:01:46.200Z"
  }
"""
When method put
Then status 400
* print 'response is: ' + response
* def idScenario = response.id
* print 'id ' + idScenario


* configure headers = headerAdminData
Given url baseUrl + 'scenarios/' + idScenario
When method delete
Then status 204
