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
from googlecode_upload import upload_find_auth
from re import match

__author__='Kevin Brightwell (Nava2)'

def get_rev():
    '''Calls `svn info` and parses the output from the directory.
    '''
    # call `svn info' to get the rev.
    p = subprocess.Popen(['svn info'], stdout=subprocess.PIPE,shell=True)
    
    #trim the numbers out of the input.
    rev_string = p.stdout.readlines()[4]
    return rev_string.strip(' Revison:\n');
        

def tar_repos(use_temp=None,exclude_files=None):
    # TODO: MAKE THIS USE TEMP FILES.
    if not use_temp or use_temp == True:
        file = tempfile.NamedTemporaryFile()
    else:
        file = open(os.getcwd()+'tmpArchive'+get_rev()+'.tar.bz2','w+b')
    print 'created file', file
    tar_obj = tarfile.open(fileobj=file, mode='w:bz2')
    print 'Created archive:',tar_obj
    
    os.chdir('../../')
    print 'In directory:', os.getcwd()
    
    dir = get_rev()
    is_ex = lambda p: match(str(exclude_files), p) 
    tar_obj.add('../Reflection', exclude=is_ex)
    
    tar_obj.close()

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

    tar_repos(use_temp=options.temp)
    print get_rev()
    
if __name__ == '__main__':
    sys.exit(main())
