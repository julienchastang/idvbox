/*
 * Copyright 1997-2012 Unidata Program Center/University Corporation for
 * Atmospheric Research, P.O. Box 3000, Boulder, CO 80307,
 * support@unidata.ucar.edu.
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package edu.ucar.idvbox;


import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

import org.apache.commons.codec.binary.Base64;

import org.w3c.dom.Element;

import ucar.unidata.idv.IntegratedDataViewer;
import ucar.unidata.idv.ViewManager;
import ucar.unidata.idv.publish.IdvPublisher;
import ucar.unidata.util.LogUtil;


import java.awt.Desktop;
import java.awt.Image;

import java.io.File;
import java.io.FileInputStream;

import java.net.URL;

import javax.swing.JOptionPane;


/**
 * Dropbox publisher plugin.
 *
 */
public class DropboxPublisher extends IdvPublisher {

    /**
     * Dropbox publisher name.
     */
    private static final String DROPBOX_PUBLISHER = "Dropbox publisher";

    /**
     * Dropbox API parameterized with a WebAuthSession.
     */
    private DropboxAPI<WebAuthSession> api;

    /**
     * Instantiates a new dropbox publisher.
     */
    public DropboxPublisher() {}

    /**
     * Instantiates a new dropbox publisher.
     *
     * @param idv
     *            the idv
     * @param element
     *            the element
     */
    public DropboxPublisher(IntegratedDataViewer idv, Element element) {
        super(idv, element);
    }

    /**
     * {@inheritDoc}
     */
    public void setName(String s) {
        super.setName(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return (api == null)
               ? super.getName()
               : DROPBOX_PUBLISHER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeName() {
        return DROPBOX_PUBLISHER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean configurexxx() {
        return doInitNew();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure() {
        doInitNew();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean doInitNew() {
        /*
         * See https:forums.dropbox.com/topic.php?id=62874
         *
         * Dropbox API lacks a good way of securing the app key/secret pair,
         * though the user will always have to provide credentials for any file
         * transfer to dropbox. The theoretical danger is it would be trivial
         * for someone to grab the app key/secret pair and masquerade as this
         * plugin application. But again, the user still needs to authenticate
         * in all cases. Below, using BASE 64 encoding so that the app
         * key/secret pair is at least not available as clear text.
         *
         * Please provide a base 64 encode app key/secret pair from dropbox in a
         * dropbox.properties file src/resources. Do not store in version
         * control.
         */

        try {
            String dropbox_app_key =
                new String(
                    Base64.decodeBase64(
                        getIdv().getArgsManager().getProperty(
                            "dropbox_app_key", null).getBytes()));
            String dropbox_app_secret =
                new String(
                    Base64.decodeBase64(
                        getIdv().getArgsManager().getProperty(
                            "dropbox_app_secret", null).getBytes()));

            AppKeyPair appKeys = new AppKeyPair(dropbox_app_key,
                                     dropbox_app_secret);
            WebAuthSession session = new WebAuthSession(appKeys,
                                         AccessType.APP_FOLDER);
            WebAuthInfo      authInfo = session.getAuthInfo();
            RequestTokenPair pair     = authInfo.requestTokenPair;

            String           url      = authInfo.url;
            Desktop.getDesktop().browse(new URL(url).toURI());
            JOptionPane.showMessageDialog(
                null,
                "Invoking brower to sign in to Dropbox. Press OK after you have authenticated.");

            session.retrieveWebAccessToken(pair);
            api = new DropboxAPI<WebAuthSession>(session);
        } catch (Exception e) {
            LogUtil.logException("Dropbox publishing", e);
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishContent(String contentFile,
                               ViewManager fromViewManager) {
        if ((api == null) || !api.getSession().isLinked()) {
            doInitNew();
        }
        File            f = new File(contentFile);
        FileInputStream fis;
        try {
            fis = new FileInputStream(f);
            api.putFile(f.getName(), fis, f.length(), null, null);
            fis.close();
        } catch (Exception e) {
            LogUtil.logException("Dropbox publishing", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void publishIslImage(Element tag, Image image) {}

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        if ((api != null) && api.getSession().isLinked()) {
            return super.toString() + "  (connected)";
        }
        return super.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doPublish() {}
}
