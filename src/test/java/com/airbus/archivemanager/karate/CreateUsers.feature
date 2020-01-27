Feature: Validation of Authentication process   
    


#Background:
* url baseUrl
* headerAdminData headerAdminData


@CreateUser
Scenario Outline: Create user: <user>

* configure headers = headerAdminData
Given url baseUrl + 'register'
And request {"authorities": ["ROLE_USER"], "email": "<user>@test.eu", "login": "<user>", "password": "<password>" }
When method post
#No test, if user already exit, is not important 

* configure headers = headerAdminData
Given url  baseUrl + 'users/<user>' 
When method get
Then status 200
* print 'response is: ' + response

* def userDto = response
* set userDto.activated = "true"
Given url  baseUrl + 'users/'
And request userDto
When method put
Then status 200
* print 'response is: ' + response




Examples:
  | user     | password   | 
  | user1    | user1      |   
  | user2    | user2      |   
