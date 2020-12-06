const Ebay = require("ebay-node-api");

let ebay = new Ebay({
  env: "PRODUCTION",
  clientID: "TunPhm-MyApp-PRD-ee65479ab-79e4e039",
  clientSecret: "PRD-e65479abf069-7eec-44fa-942e-e546",
  body: {
    grant_type: "client_credentials",
    scope: "https://api.ebay.com/oauth/api_scope",
  },
});

export default ebay;
