from django.conf.urls import patterns, include, url
from django.contrib import admin

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
admin.autodiscover()

urlpatterns = patterns('',
    url(r'^$', 'lifetracker.views.index', name='index'),
    url(r'^login.html$', 'lifetracker.views.login', name="login"),
    url(r'^register.html$', 'lifetracker.views.register', name='register'),
    url(r'^addEvent.html$', 'lifetracker.views.addEvent', name='addEvent'),
    url(r'^home.html$', 'lifetracker.views.home', name='home'),
    url(r'^logout.html$', 'lifetracker.views.logout', name='logout'),
    url(r'^analysis.html$', 'lifetracker.views.analysis', name='analysis'),
    url(r'^forgotAccount.html$', 'lifetracker.views.forgotAccount', name="forgotAccount"),
    url(r'^timeline.html$', 'lifetracker.views.timeline', name='timeline'),
    url(r'^deleteEvent/(\d+)', 'lifetracker.views.deleteEvent', name='deleteEvent'),
   url(r'^editEvent/(\d+)', 'lifetracker.views.editEvent', name='editEvent'),

    # url(r'^lifeTrackerSite/', include('lifeTrackerSite.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
)
