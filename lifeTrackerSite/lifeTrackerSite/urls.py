from django.conf.urls import patterns, include, url
from django.contrib import admin

from django.contrib.staticfiles.urls import staticfiles_urlpatterns


# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
admin.autodiscover()

from dajaxice.core import dajaxice_autodiscover, dajaxice_config
dajaxice_autodiscover()

urlpatterns = patterns('',
    url(r'^$', 'lifetracker.views.index', name='index'),
    url(r'^about/$', 'lifetracker.views.about', name='about'),
    url(r'^contact/$', 'lifetracker.views.contact', name='contact'),
    url(r'^login/$', 'lifetracker.views.login', name="login"),
    url(r'^register/$', 'lifetracker.views.register', name='register'),
    url(r'^addEvent/$', 'lifetracker.views.addEvent', name='addEvent'),
    url(r'^home/$', 'lifetracker.views.home', name='home'),
    url(r'^logout/$', 'lifetracker.views.logout', name='logout'),
    url(r'^analysis/$', 'lifetracker.views.analysis', name='analysis'),
    url(r'^forgotAccount/$', 'lifetracker.views.forgotAccount', name="forgotAccount"),
    url(r'^deleteEvent/(\d+)', 'lifetracker.views.deleteEvent', name='deleteEvent'),
    url(r'^editEvent/(\d+)', 'lifetracker.views.editEvent', name='editEvent'),
    url(r'^checkUsername/', 'lifetracker.views.isUsernameAvailable', name='isUsernameAvailable'),

    # url(r'^lifeTrackerSite/', include('lifeTrackerSite.foo.urls')),

    # Uncomment the admin/doc line below to enable admin documentation:
    # url(r'^admin/doc/', include('django.contrib.admindocs.urls')),

    # Uncomment the next line to enable the admin:
    url(r'^admin/', include(admin.site.urls)),
        url(dajaxice_config.dajaxice_url, include('dajaxice.urls')),                    

)



urlpatterns += patterns('',
    url(dajaxice_config.dajaxice_url, include('dajaxice.urls')),                    
)

urlpatterns += staticfiles_urlpatterns()
