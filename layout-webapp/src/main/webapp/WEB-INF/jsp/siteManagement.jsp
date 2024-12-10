<%@ page import="org.exoplatform.portal.config.UserPortalConfigService" %>
<%@ page import="org.exoplatform.container.PortalContainer" %>
<%
  PortalContainer portalContainer = PortalContainer.getCurrentInstance(session.getServletContext());
  UserPortalConfigService userPortalConfigService = portalContainer.getComponentInstanceOfType(UserPortalConfigService.class);
  String defaultPortalName = userPortalConfigService.getMetaPortal();
  String globalPortalName = userPortalConfigService.getGlobalPortal();
%>
<div class="VuetifyApp singlePageApplication">
  <div id="siteManagement">
    <script type="text/javascript">
      eXo.env.portal.defaultPortalName = '<%=defaultPortalName%>';
      eXo.env.portal.globalPortalName = '<%=globalPortalName%>';
      require(['PORTLET/layout/SiteManagement'], app => app.init());
    </script>
  </div>
</div>
