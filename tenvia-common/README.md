# Tenvia - Common Module

A shared module containing common data for Tenvia services.

## Overview

This module is not an application. It exists only to house the common configurations and DTOs used by other services.

## Usage

This module is used as a dependency.

During the build process, this module is built as a `.jar` and installed in the local `.m2` so that other services can
resolve it as a dependency when they are built.