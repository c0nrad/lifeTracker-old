from django.http import HttpResponse, HttpResponseRedirect
from django.template import RequestContext
from django.views.decorators.csrf import csrf_protect
from django.core.context_processors import csrf
from django.shortcuts import render_to_response, get_object_or_404
from django.contrib.auth import authenticate
from django.contrib.auth import login as auth_login, logout as auth_logout
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from lifetracker.models import Event
from lifetracker.forms import RegistrationForm, LoginForm, AddEventForm, ForgotAccountForm
from analysis import ratioOfLabelUsage, labelList

def index(request):
    return render_to_response('index.html', {}, context_instance=RequestContext(request))

def timeline(request):
    events = request.user.profile.events.all()
    return render_to_response('timeline.html', { "events" : events}, context_instance=RequestContext(request))

@csrf_protect
def login(request):
    if request.method == "POST":
        form = LoginForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            user = authenticate(username=username, password=password)
            if user is not None:
                if user.is_active:
                    auth_login(request,user)
                    return HttpResponseRedirect('home.html')
                else:
                    return HttpResponseRedirect('disabledAccount.html')

    form = LoginForm()
    return render_to_response('login.html', {
        'form' : form,
    }, context_instance=RequestContext(request))

def logout(request):
    auth_logout(request)
    return HttpResponseRedirect('/')

def home(request):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('login.html')

    return render_to_response('home.html', {
        'events' : request.user.profile.events.all(), 
        'user' : request.user
    }, context_instance=RequestContext(request))

@csrf_protect

def register(request):
    """
    Register a new user.

    Possible Errors:
    ERROR_USERNAME_TAKEN => Username already exist    
    """
    errors = []
    form = RegistrationForm() 
    if request.method == "POST":
        form = RegistrationForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            email = form.cleaned_data['email']
            password = form.cleaned_data['password']
            password1 = form.cleaned_data['password1']

            if User.objects.filter(username=username).count():
                errors.append("ERROR_USERNAME_TAKEN")
                return render_to_response('register.html', {
                    'errors' : errors,
                    'form' : form,
                }, context_instance=RequestContext(request))
            else:
                user = User.objects.create_user(username, email, password)

            user = authenticate(username=username, password=password)
            if user is not None:
                if user.is_active:
                    auth_login(request,user)
                    return HttpResponseRedirect('home.html')
                else:
                    return HttpResponseRedirect('disabledAccount.html')
            else:
                # XXX: Error
                print "[-] Something went wrong, user was created, but now it can't be found?"
                print username, request
            
    return render_to_response('register.html', {
        'errors' : errors,
        'form': form,
    }, context_instance=RequestContext(request))

@csrf_protect
def addEvent(request):
    if request.method == "POST":
        form = AddEventForm(request.POST, request=request)
        if form.is_valid():
            label = form.cleaned_data['label']
            startdatetime = form.cleaned_data['startdatetime']
            enddatetime = form.cleaned_data['enddatetime']
            description = form.cleaned_data['description']
            
            event = Event(label=label, startdatetime=startdatetime, enddatetime=enddatetime, description=description)
            event.save()

            event.save()
            
            # Tie event to user
            request.user.profile.events.add(event)
            return HttpResponseRedirect('home.html')
        print form.errors
    form = AddEventForm(request=request)
    return render_to_response('addEvent.html', {
        'form' : form,
        'labels' : labelList(request)
    }, context_instance=RequestContext(request))

def analysis(request):
    return render_to_response('analysis.html', {
        "charts" : [{
            "title": "Ratio of Label Usage",
            "data" : ratioOfLabelUsage(request),
            "id" : "chart_div0",
            "type" : "PieChart",
            "columns" : [{ "x": "string", "y" : "Toppings"}, {"x": 'number', "y": "Slices" }]
        }, {
            "title": "Duration Of Events",
            "data" : ratioOfLabelUsage(request),
            "id" : "chart_div1",
            "type" : "ColumnChart",
            "columns" : [{ "x": "string", "y" : "Event"}, {"x": 'number', "y": "Duraction" }]
        }]
        }, context_instance=RequestContext(request))

def forgotAccount(request):
    form = ForgotAccountForm()
    return render_to_response('forgotAccount.html', {
        "form" : form
    }, context_instance=RequestContext(request))

def deleteEvent(request, eventID):
    if not request.user.is_authenticated():
        return HttpResponseRedirect('login.html')

    try:
        event = request.user.profile.events.get(pk=eventID)
        event.delete();
        message = "Event successfully deleted."
    except Event.DoesNotExist:
        message = "Event does not belong to user."
        
    return render_to_response('deleteEvent.html', {
        'message': message
    }, context_instance=RequestContext(request))

@login_required
def editEvent(request, eventID):
    return render_to_response('editEvent.html', {})
        