# mad-stats echo service

Simple statistics echo service for mad.itera.no.

The service exposes three endpoints:
 - `GET /` used by mad.itera.no to subscribe on statistics events
 - `POST /` used by all microservices to publish a statistics event
 - `GET /health` used by the health check service to check

## Publishing statistics

The `POST /` is used to publish stats to the statistics service. This endpoint should be
used by services to generate statistics. The endpoint is called with the following
json payload.

```
{
  persist: true|false - default is true
  service: <service-name>
  topic: <user-defined-topic>
  key: <user-defined-key>
  value: <user-defined-value>
}
```

All the user defined properties is up to the service it self and will be passed without
conversion to the statistics frontend.

### Example

Lets say we want to notify the statistics service that our service `mad-backend` have
successfully handled a save event on the `articles` table.

This could translate to the following stats event:

```
{
  "service": "mad-backend",
  "topic": "crud",
  "key": "create",
  "value": {
    "type": "article",
    "author": "Håvard Høiby",
    "title": "The Mad Statter"
  }
}
```
Try it out by running this curl command:
```
curl -i \
  -H "Content-Type: application/json" \
  -X POST -d '{"persist":false,"service":"mad-backend","topic":"crud","key":"create","value":"{type:\"article\",\"author\":\"Håvard Høiby\",\"title\":\"The Mad Statter\"}"}' \
  https://mad.itera.no/api/stats/
```
Note: The `persist` flag is set to `false`. This tells mad-stats that is should not store the event.
Mad-stats will forward the event to any current listeners, but it will not persist the event. This is
is handy when testing the service.

Note: Currently the value of all fields must be strings. The value of the `value` property above
is passed to the service as a json object in a string.

## Subscribing to statistics

The `GET /` endpoint is used to subscribe on statistics events. It is used by `mad.itera.no/stats`.
The events are published to all subscribing consumers using Server Sent Events (text/event-stream).
Each event is a stats object with the following payload:

```
{
  persist: false,
  uuid: <uuid generated by mad-stats>
  timestamp: <timestamp set by mad-stats>
  service: <service-name>
  topic: <user-defined-topic>
  key: <user-defined-key>
  value: <user-defined-value>
}
```

## Health check

The `GET /health` check can be called to check if the service is alive.
The health check returns a result conforming to the following json contract.
When the service is healthy a HTTP status of `200 OK` is returned.

```
{
   "status": "ok" | "warning" | "critical",
   "problems": [
     {name: "postgres", message: "Cannot connect", status: "ok" | "warning" | "critical"}
   ]
 }
```