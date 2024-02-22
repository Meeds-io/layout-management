<%@ page import="org.exoplatform.navigation.webui.Utils" %>
<%@ page import="org.exoplatform.portal.mop.user.UserNavigation" %>
<%@ page import="io.meeds.layout.utils.SiteNavigationUtils" %>

<%
  UserNavigation userNavigation = Utils.getSelectedNavigation();
  String siteName = userNavigation.getKey().getName();
  boolean isAdministrator = SiteNavigationUtils.isAdministrator();
  boolean canManageSiteNavigation = Utils.hasEditPermissionOnNavigation() || Utils.hasEditPermissionOnPortal();
%>
<div class="VuetifyApp">
  <div id="siteNavigation">
    <script type="text/javascript">
        eXo.env.portal.siteKeyName = '<%=siteName%>';
        eXo.env.portal.isAdministrator = <%=isAdministrator%>;
      require(['PORTLET/layout/SiteNavigation'], app => app.init(<%=canManageSiteNavigation%>));
    </script>
  </div>
</div>
