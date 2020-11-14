import {
  Container,
  Box,
  Typography,
  TextField,
  CircularProgress,
  Button,
} from "@material-ui/core";
import { firebaseAuth, firestore } from "../firebase";
import React, { Component } from "react";
import logo from "../media/logo.png";

class login extends Component {
  constructor(props) {
    super(props);

    this.state = {
      email: "",
      password: "",
      show_progress: false,
    };

    this.handleChange = this.handleChange.bind();
    this.login = this.login.bind();
  }

  handleChange = (e) => {
    this.setState({
      [e.target.name]: e.target.value,
    });
  };

  login = () => {
    let valid_data = true;
    this.state.email_error = null;
    this.state.password_error = null;

    if (this.state.email === "") {
      this.state.email_error = "Required!";
      valid_data = false;
    }
    if (this.state.password === "") {
      this.state.password_error = "Required!";
      valid_data = false;
    }
    if (valid_data) {
      this.state.show_progress = true;
    }
    this.setState({
      update: true,
    });
    if (valid_data) {
      firestore
        .collection("USERS")
        .where("email", "==", this.state.email)
        .where("isAdmin", "==", true)
        .get()
        .then((querySnapshot) => {
          if (!querySnapshot.empty) {
            firebaseAuth
              .signInWithEmailAndPassword(this.state.email, this.state.password)
              .then((res) => {
                this.props.history.replace("/");
              })
              .catch((err) => {
                if (err.code === "auth/wrong-password") {
                  this.state.password_error = "Incorrect Password!";
                }
                this.setState({
                  show_progress: false,
                });
              });
          } else {
            this.state.email_error = "Not allowed!";
            this.setState({
              show_progress: false,
            });
          }
        });
    }
  };

  render() {
    return (
      <Container maxWidth="sm">
        <Box
          bgcolor="white"
          textAlign="center"
          boxShadow="2"
          borderRadius="15px"
          p="24px"
          mt="50px"
        >
          <img src={logo} height="80px" />
          <Typography variant="h5" color="textSecondary">
            ADMIN
          </Typography>
          <TextField
            label="Email"
            id="outlined-size-small"
            name="email"
            onChange={this.handleChange}
            error={this.state.email_error != null}
            helperText={this.state.email_error}
            variant="outlined"
            fullWidth
            size="small"
            margin="normal"
          />
          <TextField
            label="Password"
            id="outlined-size-small"
            type="password"
            name="password"
            onChange={this.handleChange}
            error={this.state.password_error != null}
            helperText={this.state.password_error}
            variant="outlined"
            fullWidth
            size="small"
            margin="normal"
          />
          <br />
          <br />
          {this.state.show_progress ? (
            <CircularProgress size="24px" color="secondary" />
          ) : null}
          <br />
          <Button
            disableElevation
            variant="contained"
            color="primary"
            fullWidth
            onClick={this.login}
          >
            Login
          </Button>
        </Box>
      </Container>
    );
  }
}

export default login;
