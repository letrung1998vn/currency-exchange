# API url 
1. /currency/add-exchange-rate: Add exchange rate from db
2. /currency/get-exchange-rate: get currency exchange by currency code
3. /currency/get-exchange-rate-at-time: get currency exchange by currency code at specific time
4. /currency/modify-exchange-rate: update exchange rate from db
5. /currency/get-fxds-exchange-rate: get currency exchange from external API FXDS
6. /currency/delete-exchange-rate: delete currency exchange by currency code
7. /currency/delete-exchange-rate-at-time: delete currency exchange by currency code at specific time
8. /rsa/generate: generate public key and return base64 encode public key to user
9. /rsa/encrypt: encrypt plain text by public key 
10. /currency/get-exchange-rate-with-encrypt-currency-code: get currency exchange by encrypted currency code

# Must item include:

1. Currency DB maintenance function:
   1. Add exchange rate: /currency/add-exchange-rate
   2. Get exchange rate: /currency/get-exchange-rate
   3. Get exchange rate at specific time: /currency/get-exchange-rate-at-time
   4. Modify exchange rate: /currency/modify-exchange-rate
   5. Delete exchange rate: /currency/delete-exchange-rate
   6. Delete exchange rate at specific time: /currency/delete-exchange-rate-at-time
2. Call API:
   1. /currency/call-fxds-exchange-rate: call external API FXDS
3. Call API and convert the data to form a new API:
   1. /currency/get-fxds-exchange-rate: get currency exchange from external API FXDS
4. All features must include unit tests.
5. Schedule synchronization of exchange rates:
    1. A scheduled task that runs every day at 17h30 GMT+07 to synchronize exchange rates from the external API FXDS and update into database accordingly(currently only update 2 currency VND and EUR).
   2. The scheduled is CurrencySyncService

# New Item included
The following was discovered as part of building this project:

1. Print out the request and response body log of all API be called and call out
     external APIs.
2. swagger-ui: url swagger: /swagger-ui
3. i18n design
4. Implementation of more than 2 design patterns: Builder, Adapter, Interceptor
5. Able to run on Docker
6. Error handling to decorate all API responses
7. Application of encryption and decryption technology: 
   * In this project, RSA encryption algorithm is applied to encrypt currency code when calling API /currency/get-exchange-rate-with-encrypt-currency-code
   * First generate public and private key pair by API /rsa/generate
   * Then encrypt currency code by public key by API /rsa/encrypt
   * Finally call API /currency/get-exchange-rate-with-encrypt-currency-code to get exchange rate by encrypted currency code.