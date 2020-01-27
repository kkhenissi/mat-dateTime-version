Feature: Validation of Authentication process   
    


#Background:
* url baseUrl

##############################################
@authentVerify
Scenario Outline: Verify authenticate <comment>

Given url baseUrl + 'authenticate'
And request { "username": '<username>',	"rememberMe": '<rememberMe>', "password": '<password>'} 
When method post
Then status <val>
* print 'response is: ' + response
#* def jwt = get response.id_token
#* print 'jwt: ' + jwt

Examples:
  | username   | rememberMe  | password         | val | comment           |
  | admin2     | true        | admin            | 401 | User not exit     |
  | admin      | true        | admin1           | 401 | bad password      |
  | admin      | true        | admin            | 200 | login admin  Ok   |
