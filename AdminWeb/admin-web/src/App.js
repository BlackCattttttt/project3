import React from "react";
import { Switch, Route, Redirect } from "react-router-dom";
import Authenticated from "./Component/Authenticated";
import Dashboard from "./Pages/Dashboard";
import login from "./Pages/login";

function App() {
  return (
    <Switch>
      <Route exact path="/">
        <Authenticated>
          <Dashboard />
        </Authenticated>
      </Route>
      <Route exact path="/login" component={login}>
        <Authenticated nonAuthenticated={true}>
          <login />
        </Authenticated>
      </Route>
      <Route path="*" render={() => "404 Not found!"} />
    </Switch>
  );
}

export default App;
