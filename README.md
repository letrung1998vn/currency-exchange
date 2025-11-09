# API url 
1. /add-exchange-rate: Add exchange rate from db
2. /get-exchange-rate: get currency exchange by currency code
3. /get-exchange-rate-at-time: get currency exchange by currency code at specific time
4. /modify-exchange-rate: update exchange rate from db
5. /get-fxds-exchange-rate: get currency exchange from external API FXDS
6. /delete-exchange-rate: delete currency exchange by currency code
7. /delete-exchange-rate-at-time: delete currency exchange by currency code at specific time

# New Item included
The following was discovered as part of building this project:

1. Print out the request and response body log of all API be called and call out
     external APIs.
2. swagger-ui
3. i18n design
4. Implementation of more than 2 design patterns: Builder, Adapter, Interceptor
5. Able to run on Docker
6. Error handling to decorate all API responses
7. 