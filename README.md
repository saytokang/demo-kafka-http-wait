# http 에서 kafka topic 에 request / response 각각 연동하는 예제.

http request 를 request-topic 에 넣고, response-topic 에 응답이 오면, 요청 id에 해당하는 응답을 찾아서 
매칭되는 응답으로 처리.

