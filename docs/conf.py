#
# Copyright (c) 2017, 2020 ADLINK Technology Inc.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License 2.0 which is available at
# http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
# which is available at https://www.apache.org/licenses/LICENSE-2.0.
#
# SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
#
# Contributors:
#   ADLINK zenoh team, <zenoh@adlink-labs.tech>
#

# Configuration file for the Sphinx documentation builder.

# -- Project information -----------------------------------------------------
project = 'zenoh-java'
copyright = '2020, Eclipse Foundation'
author = 'Eclipse Foundation'
release = '0.5.0-SNAPSHOT'

# -- General configuration ---------------------------------------------------
master_doc = 'index'
extensions = ['javasphinx']
language = 'java'

# -- Options for HTML output -------------------------------------------------
html_theme = 'sphinx_rtd_theme'


def run_javasphinx_apidoc(_):
    from javasphinx import apidoc
    apidoc.main([
        "javasphinx-apidoc", 
        "-f", 
        "-t", "API Refrence",
        "-o", ".",
        "../zenoh/src/main/java/"])

def setup(app):
    app.connect('builder-inited', run_javasphinx_apidoc)
