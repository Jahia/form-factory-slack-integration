package org.jahia.modules.slackmodule.actions;

import com.mashape.unirest.http.Unirest;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Created by alexander karmanov on 2016-04-19.
 */
public class SlackNotificationAction extends Action {
    private int submissionCount = 0;
    private int submissionThreshold = -1;
    private String slackHook = null;
    private String slackMessage = null;

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        submissionCount++;
        setActionProperties(session, renderContext.getSite().getName(), renderContext.getMainResource().getNode().getName());
        if (submissionCount % submissionThreshold == 0) {
            String url = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/cms/edit/default/" + renderContext.getUILocale().toString() + "/sites/" +renderContext.getSite().getName()+ ".results.html";
            Unirest.post(slackHook).body("{\"text\": \""+slackMessage+" "+url+"\", \"icon_emoji\": \":email:\",\"username\":\"You have "+submissionCount+" new submission(s)\"}").asJson();
            submissionCount = 0;
        }
        ActionResult actionResult = new ActionResult(HttpServletResponse.SC_OK);
        return actionResult;
    }

    private void setActionProperties(JCRSessionWrapper session, String siteName, String formName) throws RepositoryException {
        String query = "SELECT * FROM [fcnt:slackNotificationAction] AS result WHERE ISDESCENDANTNODE(result, '/sites/" + siteName + "/formFactory/forms/"+formName+"/actions')";
        QueryManager qm = session.getWorkspace().getQueryManager();
        Query q = qm.createQuery(query, Query.JCR_SQL2);
        QueryResult result = q.execute();
        NodeIterator ni = result.getNodes();
        JCRNodeWrapper slackNotificationAction = (JCRNodeWrapper) ni.nextNode();
        submissionThreshold = (int) slackNotificationAction.getNode("submissionThreshold").getProperty("jsonValue").getValue().getLong();
        slackHook = slackNotificationAction.getNode("slackHook").getProperty("jsonValue").getValue().getString();
        slackMessage = slackNotificationAction.getNode("slackMessage").getProperty("jsonValue").getValue().getString();
    }
}
