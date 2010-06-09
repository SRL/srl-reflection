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


def get_rev():
    '''Calls `svn info` and parses the output from the directory.
    '''
    # call `svn info' to get the rev.
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
    # TODO: MAKE THIS USE TEMP FILES.
    # create tar object and file object, for archiving
    tmppath = '%s/Reflection Stable [%s].tar.bz2' % (os.getcwd(),get_rev())
    if os.path.exists(tmppath):
        os.remove(tmppath)
        print 'Removed old file.'
    file = open(tmppath,'w+b')
    print 'created file', tmppath
    tar_obj = tarfile.open(name=file.name, mode='w:bz2')
    print 'Created archive:',tar_obj.name
    
    os.chdir('../../')
    print 'In directory:', os.getcwd()
    
    if not exclude_files:
        exclude_files = ['^\.svn']
    else:
        exclude_files.append('^\.svn')
    
    # add the reflection files to the .tar.bz2
    print 'Starting tar.'
    dir = get_rev()
    os.chdir('../')
    tar_obj.add('./Reflection/')#exclude=(lambda x: regex_match_arr(exclude_files,x)))
    os.chdir('./Reflection')
    
    tar_obj.close()
    file.close()
    
    summary = 'Stable Version, version '+get_rev()
    
    status,reason,url = upload(tmppath,_project,user,passwd,summary,'Featured')
  
    if url:
        print 'The file was uploaded successfully to: %s' % url
    else:
        print 'An error occurred. Your file was not uploaded.'
        print 'Google Code upload server said: %s (%s)' % (reason, status)

    
def main():
    '''
    '''
    parser = optparse.OptionParser(usage='update-stable.py');
    parser.add_option('-u', '--username', dest='username',
                      help='Your google code username.')
    parser.add_option('-w', '--password', dest='password',
                      help='Your google code password.')
    parser.add_option('-e', '--exclude', dest='exclude',
                      help='Files to exclude in archive.')
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
    print get_rev()
    
if __name__ == '__main__':
    sys.exit(main())
