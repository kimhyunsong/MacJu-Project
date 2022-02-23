import {BrowserRouter as Router, Switch, Route  } from "react-router-dom"
import PostList from "./routes/Post/PostList";
import PostDetail from "./routes/Post/PostDetail";
import PostCreate from "./routes/Post/PostCreate";
import BeerList from './routes/Beer/BeerList.js';
import Signup from './routes/User/Signup';
import Login from './routes/User/Login';
import Navbar from './components/Navbar.js';
import Footer from './components/Footer.js';
import BeerDetail from './routes/Beer/BeerDetail';
import Profile from "routes/User/Profile";
import Home from "routes/Home";
import PageNotFound from "components/PageNotFound";
import ProfileEdit from "routes/User/ProfileEdit";
import LoginAuth from "routes/Auth/LoginAuth";
import PageAuth from "./hoc/auth";
import Search from "routes/Search"


function App() {
  return (
    <div>
      <Router>
        <Navbar />
        <Switch>
          <Route exact path="/" component={Home} />
          <Route path="/post">
            <Switch>
              <Route exact path="/post/new" component={PageAuth(PostCreate, true)} />
              <Route path="/post/:postId" component={PageAuth(PostDetail, true)} />
              <Route exact path="/post" component={PageAuth(PostList, true)} />
              <Route path="*" component={PageNotFound} />
            </Switch>
          </Route>
          <Route path="/beer">
            <Switch>
              <Route exact path="/beer/:beerid" component={PageAuth(BeerDetail, true)} />
              <Route exact path="/beer" component={PageAuth(BeerList, true)} />
              <Route path="*" component={PageNotFound} />
            </Switch>
          </Route>
          <Route path="/user">
            <Switch>
              <Route exact path="/user/login" component={PageAuth(Login, false)} />
              <Route exact path="/user/signup" component={PageAuth(Signup, false)} />
              <Route path="*" component={PageNotFound} />
            </Switch>
          </Route>
          <Route path="/profile">
            <Switch>
              <Route exact path="/profile/edit" component={PageAuth(ProfileEdit, true)} />
              <Route path="/profile/:userid" component={PageAuth(Profile, true)} />
              <Route path="*" component={PageNotFound} />
            </Switch>
          </Route>
          <Route path="/oauth/login/response" component={PageAuth(LoginAuth, false)}/>
          <Route path="/search" component={PageAuth(Search, true)} />

          <Route path="*" component={PageNotFound} />
        </Switch>
        <Footer />
      </Router>
    </div>
  );
}



export default App;
