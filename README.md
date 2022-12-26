[![Gradle Status](https://gradleupdate.appspot.com/centic9/gpx2route/status.svg?branch=master)](https://gradleupdate.appspot.com/centic9/gpx2route/status)

This is a small tool to convert GPX files to route files that can be stored on Suunto Ambit watches via [OpenAmbit](https://github.com/openambitproject/openambit).

## Use it

### Grab it

    git clone git://github.com/centic9/gpx2route

### Build it and run tests

    cd gpx2route
    ./gradlew installDist check

### Run it

    build/dist/bin/gpx2route <name> <id> <gpx-file>
    
* This will process the given gpx-file and will produce two resulting files `routes_<id>_<name>.json` and `routes_<id>_points_<name>_points.json` 
  which can be uploaded with OpenAmbit

## Upload route to the Watch

See [Adjusting watch settings and routes without Movescount](https://github.com/openambitproject/openambit/wiki/Adjusting-watch-settings-and-routes-without-Movescount#upload-custom-routes)
for some instructions for using a tool from Openambit.

## Support this project

If you find this library useful and would like to support it, you can [Sponsor the author](https://github.com/sponsors/centic9)

## License

Copyright 2022 Dominik Stadler

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
