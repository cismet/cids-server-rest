window.onload = function() {
  var urlDoc = document.URL;
  var baseUrl=urlDoc.substring(0,urlDoc.lastIndexOf('/swagger'));
  baseUrl = baseUrl.lastIndexOf('/') !== baseUrl.length-1 ? baseUrl + '/' : baseUrl;

  window.ui = SwaggerUIBundle({
    
    url: baseUrl + 'resources',
    dom_id: '#swagger-ui',
    deepLinking: true,
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl
    ],
    layout: "StandaloneLayout"
  });

  //</editor-fold>
};
