# Handling Data

There comes the time when you need to store or provide data in your [Actions](actions.md), [Requirements](requirements.md) and [Trigger](trigger.md).  
The ART-Framework currently supports the following scenarios:

* Store data for the lifetime of the art-application run.
  *e.g.: you want*
* Store the data for yourself in a persistent manner.
  *e.g.: you have a requirement that needs to store the count for player*
* Provide data to other ART executed in the same context.
  *e.g.: you want to provide the amount of items added to a players inventory so it can be used in a text action following your action*
