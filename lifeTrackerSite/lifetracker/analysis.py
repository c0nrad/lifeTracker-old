from datetime import timedelta
from lifetracker.models import Event

def ratioOfLabelUsage(request):
    out = dict()
    for event in request.user.profile.events.all():
        if event.label in out:
            out[str(event.label)] += 1
        else:
            out[str(event.label)] = 1
    return dictToList(out)

def dictToList(inDict):
    out = []
    for key, value in inDict.iteritems():
        out.append([key, value])
    print out
    return out
    
def totalDurationPerLabel(request):
    #[[ "Sleep" : 8 ], [ "Eat", 2] ]
    for event in request.user.profile.events.all():
        if not event.enddatetime:
            continue

def labelList(request):
    labels = set()
    for event in request.user.profile.events.all():
        labels.add(str(event.label))
    return list(labels)