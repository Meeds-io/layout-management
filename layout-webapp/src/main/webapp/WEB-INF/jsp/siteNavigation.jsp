<%@page import="io.meeds.layout.service.LayoutAclService"%>
<%@page import="org.exoplatform.container.ExoContainerContext"%>
<%@page import="org.exoplatform.portal.webui.util.NavigationUtils"%>
<%@page import="org.exoplatform.portal.webui.util.Util"%>
<%@page import="org.exoplatform.portal.mop.SiteKey"%>
<%@page import="org.exoplatform.portal.mop.user.UserNavigation"%>
<%
  SiteKey siteKey = Util.getUIPortal().getSiteKey();
  UserNavigation userNavigation = NavigationUtils.getUserNavigation(Util.getPortalRequestContext().getUserPortalConfig().getUserPortal(),
                                                                    siteKey);
  LayoutAclService aclService = ExoContainerContext.getService(LayoutAclService.class);
  boolean isAdministrator = aclService.isAdministrator(request.getRemoteUser());
  boolean canManageSiteNavigation = aclService.canEditNavigation(siteKey, request.getRemoteUser());
  if (canManageSiteNavigation) {
    String siteName = userNavigation.getKey().getName(); // In case, it's global site
%>
<div class="VuetifyApp">
  <div id="siteNavigation">
    <script type="text/javascript">
      eXo.env.portal.siteKeyName = '<%=siteName%>';
      eXo.env.portal.isAdministrator = <%=isAdministrator%>;
      eXo.env.portal.canManageSiteNavigation = <%=canManageSiteNavigation%>;
      require(['PORTLET/layout/SiteNavigation'], app => app.init(<%=canManageSiteNavigation%>));
    </script>
  </div>
</div>
<% } %>
