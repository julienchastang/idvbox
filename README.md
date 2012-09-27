# idvbox

Dropbox content management for the [Unidata Integrated Data Viewer (IDV)](http://www.unidata.ucar.edu/software/idv/)

### Instructions

1. [Install the IDV](http://www.unidata.ucar.edu/software/idv/). You'll need the [nightly release version](http://www.unidata.ucar.edu/downloads/idv/nightly/index.jsp).
2. Get plugin by [sending me a note](https://github.com/julienchastang). (Email listed on page.)
3. [Install plugin](http://www.unidata.ucar.edu/software/idv/docs/userguide/misc/Plugins.html)
4. When saving content within the IDV, select Dropbox in the "Selecting Publisher" menu.
5. Your IDV content is now on Dropbox.

If you have trouble [submit an issue](https://github.com/julienchastang/idvbox/issues).

***

### Developer Zone

##### Building from source

1. `git clone git://github.com/Unidata/idvbox.git`
2. See notes in the `pom.xml` in the `dependencies` section. A few dependencies have to be manually installed.
3. Provide a `src/resources/dropbox.properties` file with a base 64 encoded app key/secret pair from [dropbox](https://www.dropbox.com/developers/apps). The property keys are `dropbox_app_key` and `dropbox_app_secret`. Do not store in version control.
4. `mvn install`
5. Copy Maven generated jar to `~/.unidata/idv/DefaultIdv/plugins/`

example `src/resources/dropbox.properties`:

    dropbox_app_key=mybase64encodedkey
    dropbox_app_secret=mybase64encodedsecret
    
    
##### Plans
1. Incorporate Dropbox API functionality such as creating sub-folders.
2. Start adding Dropbox metadata to IDV content such as thumbnails.
3. Obtain production status from Dropbox.
4. Start exploring how to share IDV content (e.g. bundles) from Dropbox.
5. Have the ability to overwrite files.