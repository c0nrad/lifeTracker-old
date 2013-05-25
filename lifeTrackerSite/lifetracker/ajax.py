from django.utils import simplejson
from dajaxice.decorators import dajaxice_register
from django.core import serializers
from lifetracker.models import Event
from datetime import datetime
from bs4 import BeautifulSoup
from debug import *
import iso8601

def hasKey(content):
    print content
    return "<input" in content

def getKey(content):
    soup = BeautifulSoup(content)
    return int(soup.find('input', attrs={'class': 'pk'})['value'])
assert getKey("LOL <input type='hidden' class='pk' value='2'/>") == 2

def getContent(content):
    return str(content.split("<input", 1)[0]).strip()
assert getContent("LOL <input type='hidden' class='pk' value='2'/>") == "LOL"

@dajaxice_register
def sayhello(request):
    return simplejson.dumps({
        'message': 'Hello world',
        'events': serializers.serialize('json', request.user.profile.events.all()),
    })

@dajaxice_register
def editEvent(request, item):
    goodMessage("editEvent: ", "item: ", item)

    if hasKey(item['content']):
        pk = getKey(item['content'])
    else:
        pk = item['key']

    infoMessage("editEvent", "pk: ", pk)

    try:
        event = request.user.profile.events.get(pk=pk)
        event.label = getContent(item['content'])
        event.startdatetime = JSONtoDatetime(item['start'])
        if 'end' in item:
            event.enddatetime = JSONtoDatetime(item['end'])
        if 'description' in item:
            event.description = item['description'].strip()
        event.save();

    except Event.DoesNotExist:
        errorMessage("editEvent: Event does not exist!")

    return simplejson.dumps({
        'events': serializers.serialize('json', request.user.profile.events.all()),
    })


@dajaxice_register
def addEvent(request, row, item):
    goodMessage("editEvent: ", "row: ", row, " item: ", item)
    event = Event(label=getContent(item['content']), startdatetime=JSONtoDatetime(item['start']), enddatetime=JSONtoDatetime(item['end']), description="")
    event.save()

    request.user.profile.events.add(event)

    return simplejson.dumps({
        'events': serializers.serialize('json', request.user.profile.events.all()),
        'pk': event.pk,
        'item': item,
        'row': row,
        })


@dajaxice_register
def deleteEvent(request, item):
    goodMessage("deleteEvent: item: ", item)
    
    pk = getKey(item['content'])
    infoMessage("editEvent", "pk: ", pk)

    try:
        event = request.user.profile.events.get(pk=pk)
        event.delete()
    except Event.DoesNotExist:
        errorMessage("deleteEvent: Event does not exist!")
    return simplejson.dumps({
        'events': serializers.serialize('json', request.user.profile.events.all()),
    })

def JSONtoDatetime(jsonDate):
    return iso8601.parse_date(jsonDate);

