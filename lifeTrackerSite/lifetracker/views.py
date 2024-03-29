from django.http import HttpResponseRedirect, HttpResponse
from django.template import RequestContext
from django.views.decorators.csrf import csrf_protect
from django.shortcuts import render_to_response, get_object_or_404
from django.contrib.auth import authenticate
from django.contrib.auth import login as auth_login, logout as auth_logout
from django.contrib.auth.models import User
from django.contrib.auth.decorators import login_required
from lifetracker.models import Event
from lifetracker.forms import ContactForm, RegistrationForm, LoginForm, EventForm, ForgotAccountForm
from analysis import ratioOfLabelUsage, labelList
from django.core.mail import send_mail
from debug import *


def index(request):
    return render_to_response('index.html', {}, context_instance=RequestContext(request))


def about(request):
    return render_to_response('about.html', {}, context_instance=RequestContext(request))


def contact(request):
    form = ContactForm(request.POST)
    if request.method == "POST":
        form = ContactForm(request.POST)
        if form.is_valid():
            name = form.cleaned_data['name'] or "Anonymous"
            email = form.cleaned_data['email'] or "Anonymous"
            message = "\nName: " + name + "\nEmail: " + email + "\n" + form.cleaned_data['message']

            send_mail('lifetracker: Contact!', message, "lifetrackersite@gmail.com",
                  ['poptarts4liffe@gmail.com', 'lifetrackersite@gmail.com'], fail_silently= False)
            return HttpResponseRedirect('/')
    return render_to_response('contact.html', {
        'form': form,
    }, context_instance=RequestContext(request))


def isUsernameAvailable(request):
    response_str = "false"
    if request.is_ajax():
        username = request.GET.get("username")
        if User.objects.filter(username=str(username)).count() >= 1:
            response_str = "false"
        else:
            response_str = "true"
    return HttpResponse("%s" % response_str)

@csrf_protect
def login(request):
    ERROR_INVALID_LOGIN = False
    if request.method == "POST":
        form = LoginForm(request.POST)
        if form.is_valid():
            username = form.cleaned_data['username']
            password = form.cleaned_data['password']
            user = authenticate(username=username, password=password)
            if user is not None:
                if user.is_active:
                    auth_login(request, user)
                    return HttpResponseRedirect('/home/')
                else:
                    return HttpResponseRedirect('disabledAccount.html')

        ERROR_INVALID_LOGIN = True
    form = LoginForm()
    return render_to_response('login.html', {
        'form': form,
        'ERROR_INVALID_LOGIN': ERROR_INVALID_LOGIN,
    }, context_instance=RequestContext(request))


@login_required
def logout(request):
    auth_logout(request)
    return HttpResponseRedirect('/')


@login_required
def home(request):

    return render_to_response('home.html', {
        'events': request.user.profile.events.all(),
        'user': request.user,
        'form': EventForm(),
        'labels': labelList(request),
        'firstTime': True,
    }, context_instance=RequestContext(request))


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

            if User.objects.filter(username=username).count() >= 1:
                errors.append("ERROR_USERNAME_TAKEN")
                errorMessage("username taken: ", username)
                return render_to_response('register.html', {
                    'errors': errors,
                    'form': form,
                }, context_instance=RequestContext(request))
            else:
                user = User.objects.create_user(username, email, password)

            user = authenticate(username=username, password=password)
            if user is not None:
                if user.is_active:
                    auth_login(request, user)
                    return HttpResponseRedirect('/home/')
                else:
                    return HttpResponseRedirect('/disabledAccount.html')
            else:
                # XXX: Error
                errorMessage('Something went wrong, user was created, but now it can\'t be found')
                print username, request
    return render_to_response('register.html', {
        'errors': errors,
        'form': form,
    }, context_instance=RequestContext(request))


@login_required
def addEvent(request):
    if request.method == "POST":
        form = EventForm(request.POST)
        if form.is_valid():
            label = form.cleaned_data['label']
            startdatetime = form.cleaned_data['startdatetime']
            enddatetime = form.cleaned_data['enddatetime']
            description = form.cleaned_data['description']

            event = Event(label=label, startdatetime=startdatetime, enddatetime=enddatetime, description=description)
            event.save()

            # Tie event to user
            request.user.profile.events.add(event)
            return HttpResponseRedirect('/home/')
        print form.errors
    form = EventForm()
    return render_to_response('addEvent.html', {
        'form': form,
        'labels': labelList(request)
    }, context_instance=RequestContext(request))


@login_required
def analysis(request):
    return render_to_response('analysis.html', {
        "charts": [{
            "title": "Ratio of Label Usage",
            "data": ratioOfLabelUsage(request),
            "id": "chart_div0",
            "type": "PieChart",
            "columns": [{"x": "string", "y": "Toppings"}, {"x": 'number', "y": "Slices"}]
        }, {
            "title": "Duration Of Events",
            "data": ratioOfLabelUsage(request),
            "id": "chart_div1",
            "type": "ColumnChart",
            "columns": [{"x": "string", "y": "Event"}, {"x": 'number', "y": "Duraction"}]
        }]
    }, context_instance=RequestContext(request))


def forgotAccount(request):
    form = ForgotAccountForm()
    return render_to_response('forgotAccount.html', {
        "form": form
    }, context_instance=RequestContext(request))


@login_required
def deleteEvent(request, eventID):
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
    instance = get_object_or_404(Event, id=eventID)
    form = EventForm(request.POST or None, instance=instance)
    if form.is_valid():
        form.save()
        return HttpResponseRedirect('/home/')
    
    return render_to_response('editEvent.html', {
        'form' : form,
        'labels': labelList(request),
    }, context_instance=RequestContext(request))

