# Configuration file for the Sphinx documentation builder.

# -- Project information -----------------------------------------------------
project = 'zenoh-java'
copyright = '2020, ATOLabs'
author = 'ATOLabs'
release = '0.4.1'

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
