# Google Sheet Kafka Connector
Connector reading data from google sheets. It uses the Google API to read entire sheet which is formatted into the JSON 
and sent to the Kafka Topic. 

## Prerequisities
* Running Strimzi in the Kubernetes cluster
* Deployed Kafka cluster
* Google account

## How To
* Create a Google Sheet
    - Note its ID from URL. The URL should be in the format `https://docs.google.com/spreadsheets/d/XYZ/edit#gid=0` - the ID here is `XYZ`
* Set `spreadSheetId` property in `02-google-sheet-connector.yaml` with the ID from previous step
* Enable Less secured applications on your Google account (https://support.google.com/accounts/answer/6010255?p=less-secure-apps&hl=en&visit_id=637544341760106607-1058988345&rd=1)
* Visit https://developers.google.com/oauthplayground/
    - Find `Google Sheets API v4`
    - Select `https://www.googleapis.com/auth/spreadsheets`
    - Click `Authorize APIs`
    - Click `Exchange authorization code for tokens`
    - Note `access_token`
* Encode string `token=access_token` using Base64 encoding. 
* Set the `google-sheet.properties` property in `00-google-sheet-creds.yaml` with the encoded secret from the previous step
* Apply kubernetes files
    - ```
      kubectl apply -f kafka-connect/00-google-sheet-creds.yaml
      kubectl apply -f kafka-connect/01-kafka-connect.yaml
      kubectl apply -f kafka-connect/01-google-sheet-connector.yaml
      ```
* Now you should see the content of Google Sheet sheet as a JSON in your Kafka Topic. You can check it by 
    ```
    kubectl exec -ti my-cluster-kafka-1 -- bash bin/kafka-console-consumer.sh --topic my-topic --from-beginning --bootstrap-server localhost:9092
    ```
    