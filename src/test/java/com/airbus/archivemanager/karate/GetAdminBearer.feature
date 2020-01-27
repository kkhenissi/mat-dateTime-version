Feature: Validation of Authentication process   
    


#Background:
* url baseUrl
* login login
* password password



@Scenario
Scenario: Get admin token

Given url baseUrl + 'authenticate'
And request { "username": '#(login)',	"rememberMe": 'true', "password": '#(password)'} 
When method post
Then status 200
* def jwt = get response.id_token
* def headerData = { Authorization: #('Bearer ' + jwt), Accept: 'application/problem+json'}

