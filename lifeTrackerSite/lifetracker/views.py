from django.http import HttpResponse, HttpResponseRedirect
from django.template import RequestContext
from django.views.decorators.csrf import csrf_protect
from django.core.context_processors import csrf
from django.shortcuts import render_to_response, get_object_or_404
from django.contrib.auth import authenticate
from django.contrib.auth import login as auth_login, logout as auth_logout
from django.contrib.auth.models import User
from lifetracker.models import Label, Event
from lifetracker.forms import RegistrationForm, LoginForm, AddEventForm, ForgotAccountForm
from analysis import occurrencesPerLabel

def index(request):
    if request.user.is_authenticated():
        return HttpResponseRedirect('home.html')
    return render_to_response('index.html', {}, context_instance=RequestContext(request))

def base(request):
    return render_to_response('base.html', {}, context_instance=RequestContext(request))

def timeline(request):
    return render_to_response('timeline.html', {}, context_instance=RequestContext(request))

def datetime(request):
    return render_to_response('datetime.html', {}, context_instance=RequestContext(request))
    
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
    if request.method == "POST":
        form = RegistrationForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            email = form.cleaned_data['email']
            password = form.cleaned_data['password']
            password1 = form.cleaned_data['password1']

            user = User.objects.create_user(username, email, password)
            return HttpResponseRedirect('login.html')
    else:
        form = RegistrationForm() 
    return render_to_response('register.html', {
        'form': form,
    }, context_instance=RequestContext(request))

@csrf_protect
def addEvent(request):
    if request.method == "POST":
        form = AddEventForm(request.POST, request=request)
        if form.is_valid():
            name = form.cleaned_data['name']
            label = form.cleaned_data['label']
            datetime = form.cleaned_data['datetime']
            description = form.cleaned_data['description']
            link = form.cleaned_data['link']
            
            if len(Label.objects.filter(value=label)) >= 1:
                label = Label.objects.get(value=label)
            else:
                label = Label(value=label)
                label.save()
                request.user.profile.labels.add(label)
                
            event = Event(name=name,label=label, datetime=datetime, description=description)
            event.save()
            if link: # Must be done after the inital save (many-to-many relationship requirement)
                event.links.add(link)
                event.save()
                link.links.add(event)
                link.save()

            # Tie event to user
            request.user.profile.events.add(event)
            return HttpResponseRedirect('home.html')
        print form.errors
    form = AddEventForm(request=request)
    
    return render_to_response('addEvent.html', {
        'form' : form
    }, context_instance=RequestContext(request))

def analysis(request):
    return render_to_response('analysis.html', {
        "title": "Occurrences per Label",
        "data" : occurrencesPerLabel(request)
    }, context_instance=RequestContext(request))

def forgotAccount(request):
    form = ForgotAccountForm()
    return render_to_response('forgotAccount.html', {
        "form" : form
    }, context_instance=RequestContext(request))