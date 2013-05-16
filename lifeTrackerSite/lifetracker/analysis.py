from lifetracker.models import Label, Event

def occurrencesPerLabel(request):
    out = []
    for label in Label.objects.all():
        value = label.value
        occurances = request.user.profile.events.filter(label=label).count()
        out.append([str(value),occurances])
    return out

def duration(eventA, eventB):
    pass