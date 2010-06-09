# -*- coding: utf-8 -*-
"""
Created on Mon Jun  7 21:50:04 2010

@author: kevin
"""

import sys
import os
import subprocess
import tempfile
import tarfile
import optparse
from googlecode_upload import upload
import re

__author__='Kevin Brightwell (Nava2)'

_project = 'simbareflection'


_updated = False
def get_rev():
    '''Calls `svn info` and parses the output from the directory.
    '''
    # call `svn info' to get the rev.
    global _updated
    if not _updated:
        print 'Updating SVN.'
        subprocess.Popen(['svn up'],shell=True)
        _updated = True
        
    p = subprocess.Popen(['svn info'], stdout=subprocess.PIPE,shell=True)
    
    #trim the numbers out of the input.
    rev_string = p.stdout.readlines()[4]
    return rev_string.strip(' Revison:\n');
        
def regex_match_arr(p, s, flags=0):
    #print s
    for i in range(len(p)):
        try:
            l = re.findall(p[i],s)
            if l:
                print l
            if re.match(p[i], s, flags=flags):
                print 'Excluding: [%s] %s' % (p[i],s)
                return True
        except:
            print 'ERROR: [%s] %s' % (p[i],s)
    else:
        return False

def tar_repos(user,passwd,use_temp=None,exclude_files=None):
    version = get_rev()
    
    # create tar object and file object, for archiving
    tmppath = '%s/Reflection_Stable.tar.bz2' % os.getcwd()
    if os.path.isfile(tmppath):
        os.remove(tmppath)
        print 'Removed old archive.'
    file = open(tmppath,'w+b')
    print 'created file', tmppath
    tar_obj = tarfile.open(name=file.name, mode='w:bz2')
    print 'Created archive:',tar_obj.name
    
    os.chdir('../../')
    
    if not exclude_files:
        exclude_files = ['^\.svn',tmppath]
    else:
        exclude_files.append('^\.svn')
        exclude_files.append(tmppath)
    
    # add the reflection files to the .tar.bz2
    print 'Starting tar.'
    os.chdir('../')
    tar_obj.add('./Reflection/',exclude=(lambda x: regex_match_arr(exclude_files,x)))
    os.chdir('./Reflection')
    
    tar_obj.close()
    file.close()
    
    # Upload the stable version
    print 'Uploading .tar'
    summary = 'Stable Version, version '+version
    
    status,reason,url = upload(tmppath,_project,user,passwd,summary,'Featured')
  
    if url:
        print 'Stable repository updated. Link: %s' % url
    else:
        print 'An error occurred. Your file was not uploaded.'
        print 'Google Code upload server said: %s (%s)' % (reason, status)
        return
    
    # Upload the version:
    tmppath = '%s\\Reflection_Version' % os.getcwd()
    if os.path.isfile(tmppath):
        os.remove(tmppath)
        print 'Removed old version file.'
    file = open(tmppath,'w+b')
    file.write(version)
    file.close()
    
    summary = 'Stable Version Number, used by updater.'
    status,reason,url = upload(tmppath,_project,user,passwd,summary,'Featured')
  
    if url:
        print 'Version Updated. Link: %s' % url
    else:
        print 'An error occurred. Your file was not uploaded.'
        print 'Google Code upload server said: %s (%s)' % (reason, status)
        return
    
def main():
    '''
    '''
    parser = optparse.OptionParser(usage='update-stable.py');
    parser.add_option('-u', '--username', dest='username',
                      help='Your google code username.')
    parser.add_option('-w', '--password', dest='password',
                      help='Your google code password.')
    parser.add_option('-e', '--exclude', dest='exclude',
                      help='Files to exclude in archive, use %s as a separator.'%';;')
    parser.add_option('-t', '--temp', dest='temp',
                      help='False to save created archive.')
    parser.add_option('-p', '--path', dest='path',
                      help='Head location for creating tar from.')
    
    options, args = parser.parse_args()
    
    if not options.username:
        parser.error('Username not specified')
    elif not options.password:
        parser.error('Password not specified')
    
    if options.exclude:
        exclude_files = options.exclude.split(';;')
    else:
        exclude_files = []

    tar_repos(options.username,options.password,use_temp=options.temp,exclude_files=exclude_files)
    
if __name__ == '__main__':
    sys.exit(main())
