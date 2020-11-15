import { Container, Box, Typography } from "@material-ui/core";
import React, { Component } from "react";
import { Line, Pie } from "react-chartjs-2";
import { firestore } from "../firebase";

export class HomeFragment extends Component {
  constructor(props) {
    super(props);

    this.state = {
      lineChartData: {},
      pieChartData: {},
      userCount: 0,
      orderCount: 0,
    };
  }

  componentDidMount() {
    this.getLineChartData();
    this.getPieChartData();
  }

  getLineChartData() {
    let curr = new Date();
    let week = [];

    let pastDate = new Date(curr.getDate() - 6);
    for (let i = 1; i <= 7; i++) {
      let first = pastDate - pastDate.getDay() + i;
      let day = new Date(curr.setDate(first)).toISOString().slice(0, 10);
      week.push(day);
    }
    let createAt = [];
    let userData = [];
    firestore
      .collection("USERS")
      .get()
      .then((querySnapshot) => {
        this.state.userCount = querySnapshot.size;
        querySnapshot.forEach(function (doc) {
          let cr = doc.get("create_at").toDate();
          createAt.push(cr);
        });
        for (let i = 0; i < week.length; i++) {
          let count = 0;
          let x = new Date(week[i]);
          for (let j = 0; j < createAt.length; j++) {
            if (createAt[j].getTime() <= x.getTime()) {
              count++;
            }
          }
          userData.push(count);
        }
        this.setState({
          lineChartData: {
            labels: week,
            datasets: [
              {
                data: userData,
                label: "user",
                borderColor: "#3e95cd",
                fill: false,
              },
            ],
          },
        });
      });
  }

  getPieChartData() {
    let orderData = [];
    for (let i = 0; i < 7; i++) {
      orderData.push(0);
    }
    firestore
      .collection("ORDERS")
      .get()
      .then((querySnapshot) => {
        this.state.orderCount = querySnapshot.size;
        querySnapshot.forEach(function (doc) {
          let status = doc.get("order_status");
          if (status === "Đã tạo") {
            orderData[0]++;
          } else if (status === "Đã thanh toán") {
            orderData[1]++;
          } else if (status === "[USA]Đã lấy hàng/Đã nhập kho") {
            orderData[2]++;
          } else if (status === "[VN]Đã lấy hàng/Đã nhập kho") {
            orderData[3]++;
          } else if (status === "[VN]Đã điều phối giao hàng/Đang giao hàng") {
            orderData[4]++;
          } else if (status === "Đã giao hàng") {
            orderData[5]++;
          } else if (status === "Đã hủy") {
            orderData[6]++;
          }
        });
        this.setState({
          pieChartData: {
            labels: [
              "Đã tạo",
              "Đã thanh toán",
              "[USA]Đã lấy hàng/Đã nhập kho",
              "[VN]Đã lấy hàng/Đã nhập kho",
              "[VN]Đã điều phối giao hàng/Đang giao hàng",
              "Đã giao hàng",
              "Đã hủy",
            ],
            datasets: [
              {
                label: "Order",
                backgroundColor: [
                  "#4BB543",
                  "#00C2C7",
                  "#82983B",
                  "#62F46E",
                  "#44AA4D",
                  "#28662E",
                  "#D9092E",
                ],
                data: orderData,
              },
            ],
          },
        });
      });
  }

  render() {
    return (
      <Container maxWidth="md">
        <Box bgcolor="white" boxShadow="2" borderRadius="15px" p="8px" mt="0px">
          <Typography
            variant="h6"
            style={{
              marginLeft: "20px",
              marginTop: "20px",
              color: "lightgray",
            }}
          >
            Users(last week)
          </Typography>
          <Typography
            variant="h4"
            style={{
              marginBottom: "35px",
              marginLeft: "20px",
              marginTop: "5px",
            }}
          >
            {this.state.userCount - 1}
          </Typography>
          <Line
            data={this.state.lineChartData}
            options={{
              title: {
                display: false,
              },
              legend: {
                display: true,
                position: "right",
              },
              scales: {
                yAxes: [
                  {
                    ticks: {
                      max: this.state.userCount + 5,
                      min: 0,
                      stepSize: 1,
                    },
                  },
                ],
              },
            }}
          />
          <br />
          <br />
        </Box>
        <Box
          bgcolor="white"
          boxShadow="2"
          borderRadius="15px"
          p="2px"
          marginTop="20px"
        >
          <Typography
            variant="h6"
            style={{
              marginLeft: "20px",
              marginTop: "20px",
              color: "lightgray",
            }}
          >
            Orders
          </Typography>
          <Typography
            variant="h4"
            style={{
              marginBottom: "15px",
              marginLeft: "20px",
              marginTop: "5px",
            }}
          >
            {this.state.orderCount}
          </Typography>
          <Pie
            data={this.state.pieChartData}
            option={{
              title: {
                display: false,
              },
              legend: {
                display: true,
                position: "right",
              },
            }}
          />
          <br />
          <br />
        </Box>
      </Container>
    );
  }
}

export default HomeFragment;
