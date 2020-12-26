import {
  Avatar,
  Box,
  Button,
  Container,
  Divider,
  Grid,
  InputAdornment,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@material-ui/core";
import React, { Component } from "react";
import { makeStyles, withStyles } from "@material-ui/core/styles";
import clsx from "clsx";
import Stepper from "@material-ui/core/Stepper";
import Step from "@material-ui/core/Step";
import StepLabel from "@material-ui/core/StepLabel";
import Check from "@material-ui/icons/Check";
import LocalShippingIcon from "@material-ui/icons/LocalShipping";
import StepConnector from "@material-ui/core/StepConnector";
import Dialog from "@material-ui/core/Dialog";
import DialogActions from "@material-ui/core/DialogActions";
import DialogContent from "@material-ui/core/DialogContent";
import DialogContentText from "@material-ui/core/DialogContentText";
import DialogTitle from "@material-ui/core/DialogTitle";
import { format } from "date-fns";
import DateFnsUtils from "@date-io/date-fns";
import {
  KeyboardDateTimePicker,
  MuiPickersUtilsProvider,
} from "@material-ui/pickers";
import { firestore } from "../firebase";

const QontoConnector = withStyles({
  alternativeLabel: {
    top: 10,
    left: "calc(-50% + 16px)",
    right: "calc(50% + 16px)",
  },
  active: {
    "& $line": {
      borderColor: "#784af4",
    },
  },
  completed: {
    "& $line": {
      borderColor: "#784af4",
    },
  },
  line: {
    borderColor: "#eaeaf0",
    borderTopWidth: 3,
    borderRadius: 1,
  },
})(StepConnector);
const useQontoStepIconStyles = makeStyles({
  root: {
    color: "#eaeaf0",
    display: "flex",
    height: 22,
    alignItems: "center",
  },
  active: {
    color: "#784af4",
  },
  completed: {
    color: "#784af4",
    zIndex: 1,
    fontSize: 18,
  },
});

function QontoStepIcon(props) {
  const classes = useQontoStepIconStyles();
  const { active, completed } = props;

  return (
    <div
      className={clsx(classes.root, {
        [classes.active]: active,
      })}
    >
      {completed ? (
        <Check className={classes.completed} />
      ) : (
        <LocalShippingIcon />
      )}
    </div>
  );
}

const useStyles = makeStyles((theme) => ({
  root: {
    width: "100%",
  },
  button: {
    marginRight: theme.spacing(1),
  },
  instructions: {
    marginTop: theme.spacing(1),
    marginBottom: theme.spacing(1),
    backgroundColor: "#44aa4d",
    color: "white",
    textAlign: "center",
  },
}));

function getSteps(date) {
  return [
    { label: "Đã tạo", date: date[0] },
    { label: "Đã thanh toán", date: date[1] },
    { label: "[USA]Đã lấy hàng/Đã nhập kho", date: date[2] },
    { label: "[VN]Đã lấy hàng/Đã nhập kho", date: date[3] },
    { label: "[VN]Đã điều phối giao hàng/Đang giao hàng", date: date[4] },
    { label: "Đã giao hàng", date: date[5] },
  ];
}

function CustomizedSteppers(props) {
  const classes = useStyles();
  //console.log(props);
  const [activeStep, setActiveStep] = React.useState(props.value - 1);
  const [open, setOpen] = React.useState(false);
  const [open1, setOpen1] = React.useState(false);
  const [cancel, setCancel] = React.useState(props.value > 6);
  const [changeWeight, setChangeWeight] = React.useState(false);

  const [selectedDate, handleDateChange] = React.useState(new Date());
  const steps = getSteps(props.date);
  const handleClickOpen = () => {
    setOpen(true);
  };
  const handleClickOpen1 = () => {
    setOpen1(true);
  };

  const handleClose1 = () => {
    setOpen1(false);
    setCancel(true);
  };

  const handleUpdate = () => {
    setActiveStep((prevActiveStep) => prevActiveStep + 1);
    if (activeStep === 3) setChangeWeight(true);
    setOpen(false);

    props.setStatus(steps[activeStep + 1].label);
    props.setDate(activeStep + 1, selectedDate);
  };
  const handleDelete = () => {
    setOpen1(false);
    setCancel(true);
    props.setDate(6, new Date());
    props.setStatus("Đã hủy");
  };
  const handleClose = () => {
    setOpen(false);
  };
  const handleNext = () => {
    handleClickOpen();
  };

  const handleCancel = () => {
    handleClickOpen1();
  };

  const changeDeposit = (e) => {
    props.setDeposit(e.target.value);
  };

  return (
    <div className={classes.root}>
      {cancel ? (
        <div>
          <Grid container spacing={3}>
            <Grid item xs={5}>
              <Typography
                variant="subtitle2"
                display="block"
                gutterBottom
                style={{ textAlign: "left", marginTop: 10 }}
              >
                Trạng thái
              </Typography>
              <Typography
                variant="body2"
                gutterBottom
                style={{ color: "white", backgroundColor: "red" }}
              >
                Đã hủy
              </Typography>
            </Grid>
            <Grid item xs={7}>
              <Typography variant="subtitle2" gutterBottom>
                Thời gian hủy
              </Typography>
              <Typography variant="body2" gutterBottom>
                {format(props.date[6], "dd/MM/yyyy kk:mm:ss")}
              </Typography>
            </Grid>
          </Grid>
        </div>
      ) : (
        <div>
          {" "}
          <Typography
            variant="subtitle2"
            display="block"
            gutterBottom
            style={{ textAlign: "left", marginTop: 10 }}
          >
            Trạng thái
          </Typography>
          <Stepper
            alternativeLabel
            activeStep={activeStep}
            connector={<QontoConnector />}
          >
            {steps.map((step) => (
              <Step key={step.label}>
                <StepLabel StepIconComponent={QontoStepIcon}>
                  {step.label}
                  <Typography variant="caption" display="block" gutterBottom>
                    {step.date === ""
                      ? step.date
                      : format(step.date, "dd/MM/yyyy kk:mm:ss")}
                  </Typography>
                </StepLabel>
              </Step>
            ))}
          </Stepper>
          <div>
            {activeStep === steps.length - 1 ? (
              <div>
                <Typography variant="h5" className={classes.instructions}>
                  Đơn hàng đã được hoàn thành
                </Typography>
              </div>
            ) : (
              <div
                style={{
                  marginLeft: "400px",
                  marginRight: "400px",
                }}
              >
                {changeWeight ? (
                  <div style={{ marginBottom: "15px" }}>
                    <a href="#outlined-product-weight">
                      Cập nhật khối lượng sản phẩm
                    </a>
                  </div>
                ) : null}
                <div>
                  <Button
                    onClick={handleCancel}
                    className={classes.button}
                    style={{ backgroundColor: "red", color: "white" }}
                  >
                    Cancel
                  </Button>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={handleNext}
                    className={classes.button}
                  >
                    {activeStep === steps.length - 1 ? "Finish" : "Next"}
                  </Button>
                </div>
              </div>
            )}
          </div>
          <Dialog
            open={open}
            onClose={handleClose}
            aria-labelledby="form-dialog-title"
            fullWidth
            maxWidth="sm"
          >
            <DialogTitle id="form-dialog-title">Cập nhật đơn hàng</DialogTitle>
            <DialogContent>
              {activeStep === 0 ? (
                <div style={{ marginBottom: "15px" }}>
                  <TextField
                    id="outlined-size-small"
                    label="Khách đã trả"
                    type="number"
                    name="deposit"
                    fullWidth
                    variant="outlined"
                    onChange={changeDeposit}
                    size="small"
                    defaultValue={props.deposit}
                    InputLabelProps={{
                      shrink: true,
                    }}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">$</InputAdornment>
                      ),
                    }}
                  ></TextField>
                </div>
              ) : null}
              <MuiPickersUtilsProvider utils={DateFnsUtils}>
                <div>
                  <KeyboardDateTimePicker
                    variant="inline"
                    ampm={false}
                    label="Thời gian"
                    value={selectedDate}
                    onChange={handleDateChange}
                    disablePast
                    fullWidth
                    format="yyyy/MM/dd HH:mm:ss"
                  />
                </div>
              </MuiPickersUtilsProvider>
            </DialogContent>
            <DialogActions>
              <Button onClick={handleClose} color="primary">
                Hủy
              </Button>
              <Button onClick={handleUpdate} color="primary">
                Cập nhật
              </Button>
            </DialogActions>
          </Dialog>
          <Dialog
            open={open1}
            onClose={handleClose1}
            aria-labelledby="alert-dialog-title"
            aria-describedby="alert-dialog-description"
          >
            <DialogTitle id="alert-dialog-title">Thông báo</DialogTitle>
            <DialogContent>
              <DialogContentText id="alert-dialog-description">
                Bạn có chắc chắn muốn hủy đơn hàng này không ?
              </DialogContentText>
            </DialogContent>
            <DialogActions>
              <Button onClick={handleClose1} color="primary">
                Không
              </Button>
              <Button onClick={handleDelete} color="primary" autoFocus>
                Xác nhận
              </Button>
            </DialogActions>
          </Dialog>
        </div>
      )}
    </div>
  );
}

class OrderDetailDialog extends Component {
  constructor(props) {
    super(props);

    this.state = {
      order: this.props.order,
      orderStatus: this.props.order.status,
      orderStatusValue: this.props.order.statusValue,
      date: [
        this.props.order.order_date,
        this.props.order.pay_date,
        this.props.order.pack_date,
        this.props.order.usa_ship_date,
        this.props.order.vn_ship_date,
        this.props.order.delivery_date,
        this.props.order.cancel_date,
      ],
      totalAmount: this.props.order.totalAmount,
      deposit: this.props.order.deposit,
      amount: this.props.order.totalAmount - this.props.order.deposit,
      weight_error: "",
      deposit_error: "",
    };
  }
  componentDidMount() {
    //console.log(this.props.order);
  }

  setDate = (step, time) => {
    this.state.date[step] = time;
  };
  setStatus = (status) => {
    this.setState({
      orderStatus: status,
    });
  };
  changeWeight = (e, index) => {
    if (parseFloat(e.target.value) > 0) {
      this.setState({
        weight_error: "",
      });
      this.props.order.product[index].weight = parseFloat(e.target.value);
      let totalamount = 0;
      for (let i = 0; i < this.props.order.product.length; i++) {
        let prd = this.props.order.product[i];
        totalamount +=
          (prd.transportfee * prd.weight + prd.price) * prd.quantity;
      }
      this.setState({
        totalAmount: Math.round(totalamount * 100) / 100,
        amount: Math.round(totalamount * 100) / 100 - this.state.deposit,
      });
    } else {
      this.setState({
        weight_error: "Khối lượng phải lớn hơn 0",
      });
    }
    // console.log(this.props.order.product);
  };
  changeDeposit = (e) => {
    if (parseFloat(e.target.value) > 0) {
      this.setState({
        [e.target.name]: e.target.value,
      });
      this.setState({
        amount: this.state.totalAmount - e.target.value,
        deposit_error: "",
      });
    } else {
      this.setState({
        deposit_error: "Số tiền phải lớn hơn 0",
      });
    }
  };
  onCancel() {
    this.props.showOrderDetail(false);
  }

  onUpdate() {
    for (let i = 0; i < this.state.date.length; i++) {
      if (this.state.date[i] === "") {
        this.state.date[i] = this.state.date[0];
      }
    }
    let editOrder = {
      order_ID: this.props.order.orderId,
      fullname: this.props.order.name,
      phone_number: this.props.order.phone,
      address: this.props.order.address,
      list_size: this.props.order.product.length,
      total_amount: this.state.totalAmount,
      deposit: this.state.deposit,
      total_item: this.props.order.totalItem,
      user_ID: this.props.order.userId,
      order_status: this.state.orderStatus,
      ordered_date: this.state.date[0],
      payed_date: this.state.date[1],
      packed_date: this.state.date[2],
      shiped_usa_date: this.state.date[3],
      shiped_vn_date: this.state.date[4],
      delivery_date: this.state.date[5],
      cancelled_date: this.state.date[6],
    };

    for (let i = 0; i < this.props.order.product.length; i++) {
      let id = "product_ID_" + i;
      let des = "product_des_" + i;
      let price = "product_price_" + i;
      let quantity = "product_quantity_" + i;
      let weight = "product_weight_" + i;
      let fee = "transportfee_" + i;
      let categoty = "category_ID_" + i;
      editOrder[id] = this.props.order.product[i].Id;
      editOrder[des] = this.props.order.product[i].description;
      editOrder[price] = this.props.order.product[i].price;
      editOrder[quantity] = this.props.order.product[i].quantity;
      editOrder[weight] = this.props.order.product[i].weight;
      editOrder[fee] = this.props.order.product[i].transportfee;
      editOrder[categoty] = this.props.order.product[i].categoryId;
    }
    console.log(editOrder);

    if (editOrder.order_status === "Đã thanh toán") {
      let notification = {
        list_size: 1,
        title_0:
          "Đơn hàng " + editOrder.order_ID + "  của bạn đã được cập nhật",
        body_0:
          "Đơn hàng " +
          editOrder.order_ID +
          "  của bạn đã được thanh toán $ " +
          editOrder.deposit,
        date_0: editOrder.payed_date,
        readed_0: true,
      };
      console.log(notification);
      firestore
        .collection("USERS")
        .doc(editOrder.user_ID)
        .collection("USER_DATA")
        .doc("MY_NOTIFICATION")
        .set(notification)
        .then(() => {
          console.log("Document successfully updated!");
          //this.getCategoryDatabase();
          //this.props.getData();
        })
        .catch(function (error) {
          // The document probably doesn't exist.
          console.error("Error updating document: ", error);
        });
    } else if (
      editOrder.order_status === "[VN]Đã điều phối giao hàng/Đang giao hàng"
    ) {
      let notification = {
        list_size: 1,
        title_0: "Đơn hàng " + editOrder.order_ID + " của bạn đã được cập nhật",
        body_0:
          "Đơn hàng " +
          editOrder.order_ID +
          "  của bạn đã được đo lại khối lượng",
        date_0: editOrder.shiped_vn_date,
        readed_0: true,
      };
      console.log(notification);
      firestore
        .collection("USERS")
        .doc(editOrder.user_ID)
        .collection("USER_DATA")
        .doc("MY_NOTIFICATION")
        .set(notification)
        .then(() => {
          console.log("Document successfully updated!");
        })
        .catch(function (error) {
          console.error("Error updating document: ", error);
        });
    }
    firestore
      .collection("ORDERS")
      .doc(this.props.order.orderId)
      .update(editOrder)
      .then(() => {
        console.log("Document successfully updated!");
        //this.getCategoryDatabase();
        this.props.getData();
      })
      .catch(function (error) {
        // The document probably doesn't exist.
        console.error("Error updating document: ", error);
      });
    this.props.showOrderDetail(false);
  }

  changeDepositDialog = (de) => {
    this.setState({
      deposit: de,
      amount: Math.round((this.state.totalAmount - de) * 100) / 100,
    });
  };
  render() {
    return (
      <div
        style={{
          width: "100vw",
          height: "120vh",
          backgroundColor: "rgb(0, 0, 0, 0.4)",
          zIndex: "100",
          position: "absolute",
          top: 0,
          left: 0,
        }}
      >
        <Container maxWidth="lg">
          <Box
            bgcolor="white"
            boxShadow="2"
            borderRadius="15px"
            p="24px"
            mt="50px"
            marginTop="90px"
            marginLeft="170px"
          >
            <Typography variant="h5" color="textprimary">
              Thông tin đơn hàng
            </Typography>
            <Divider />
            <Grid container spacing={3}>
              <Grid item xs={5}>
                <Typography
                  variant="button"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  Mã đơn hàng
                </Typography>
                <Typography
                  variant="body2"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  {this.state.order.orderId}
                </Typography>
              </Grid>
              <Grid item xs={7}>
                <Typography
                  variant="button"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  Thời gian tạo
                </Typography>
                <Typography
                  variant="body2"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  {format(this.state.order.order_date, "dd/MM/yyyy kk:mm:ss")}
                </Typography>
              </Grid>
            </Grid>
            <Divider />
            <Typography
              variant="button"
              display="block"
              gutterBottom
              style={{ textAlign: "left", marginTop: 10 }}
            >
              Tình trạng đơn hàng
            </Typography>

            <CustomizedSteppers
              value={this.state.orderStatusValue}
              date={this.state.date}
              deposit={this.state.deposit}
              setDate={this.setDate}
              setStatus={this.setStatus}
              setDeposit={this.changeDepositDialog}
            />
            <Typography
              variant="button"
              display="block"
              gutterBottom
              style={{ textAlign: "left", marginTop: 10 }}
            >
              Thông tin người nhận
            </Typography>
            <Divider />
            <Grid container spacing={3}>
              <Grid item xs={5}>
                <Typography
                  variant="subtitle2"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  Tên Người nhận
                </Typography>
                <Typography
                  variant="body2"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  {this.state.order.name}
                </Typography>
              </Grid>
              <Grid item xs={7}>
                <Typography
                  variant="subtitle2"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  Số điện thoại
                </Typography>
                <Typography
                  variant="body2"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  {this.state.order.phone}
                </Typography>
              </Grid>
            </Grid>
            <Typography
              variant="subtitle2"
              display="block"
              gutterBottom
              style={{ textAlign: "left", marginTop: 10 }}
            >
              Địa chỉ
            </Typography>
            <Typography
              variant="body2"
              display="block"
              gutterBottom
              style={{ textAlign: "left", marginTop: 10 }}
            >
              {this.state.order.address}
            </Typography>
            <Divider />
            <Typography
              variant="button"
              display="block"
              gutterBottom
              style={{ textAlign: "left", marginTop: 10 }}
            >
              Danh sách sản phẩm
            </Typography>
            <Table size="small" aria-label="purchases">
              <TableHead>
                <TableRow>
                  <TableCell width="400">Tên sản phẩm</TableCell>
                  <TableCell>Hình ảnh</TableCell>
                  <TableCell>Mô tả</TableCell>
                  <TableCell width="140" align="right">
                    Khối lượng (1 sản phẩm)
                  </TableCell>
                  <TableCell width="100" align="right">
                    Số lượng
                  </TableCell>
                  <TableCell width="100" align="right">
                    Giá tiền
                  </TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {this.state.order.product.map((productRow) => (
                  <TableRow key={productRow.title}>
                    <TableCell width="400">
                      <a href={productRow.link} target="_blank">
                        {productRow.title}
                      </a>
                    </TableCell>
                    <TableCell>
                      <Avatar variant="square" src={productRow.image}></Avatar>
                    </TableCell>
                    <TableCell>{productRow.description}</TableCell>
                    <TableCell width="140" align="right">
                      <TextField
                        id="outlined-product-weight"
                        type="number"
                        name="weight"
                        fullWidth
                        variant="outlined"
                        size="small"
                        disabled={this.state.orderStatusValue !== 4}
                        error={this.state.weight_error != null}
                        helperText={this.state.weight_error}
                        onChange={(e) => this.changeWeight(e, productRow.id)}
                        defaultValue={productRow.weight}
                        InputLabelProps={{
                          shrink: true,
                        }}
                        InputProps={{
                          startAdornment: (
                            <InputAdornment position="start">Kg</InputAdornment>
                          ),
                        }}
                      ></TextField>
                    </TableCell>
                    <TableCell width="100" align="right">
                      {productRow.quantity}
                    </TableCell>
                    <TableCell width="100" align="right">
                      ${productRow.price}
                    </TableCell>
                  </TableRow>
                ))}
                <TableRow>
                  <TableCell colSpan={4}>
                    <b>Tổng tiền hàng</b>
                  </TableCell>
                  <TableCell colSpan={2} align="right">
                    ${this.state.totalAmount}
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell colSpan={4}>
                    <b>Khách đã trả</b>
                  </TableCell>
                  <TableCell colSpan={2} align="right">
                    <TextField
                      id="outlined-size-small"
                      type="number"
                      name="deposit"
                      fullWidth
                      variant="outlined"
                      value={this.state.deposit}
                      error={this.state.deposit_error != null}
                      helperText={this.state.deposit_error}
                      onChange={this.changeDeposit}
                      disabled={
                        this.state.orderStatusValue > 5 ||
                        this.state.orderStatusValue === 1
                      }
                      size="small"
                      defaultValue={this.state.deposit}
                      InputLabelProps={{
                        shrink: true,
                      }}
                      InputProps={{
                        startAdornment: (
                          <InputAdornment position="start">$</InputAdornment>
                        ),
                      }}
                    ></TextField>
                  </TableCell>
                </TableRow>
                <TableRow>
                  <TableCell colSpan={4}>
                    <b>Còn lại</b>
                  </TableCell>
                  <TableCell colSpan={2} align="right">
                    ${this.state.amount}
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>

            {this.state.orderStatusValue < 6 ? (
              <Grid container spacing={3} style={{ marginTop: "15px" }}>
                <Grid item xs={1}></Grid>
                <Grid item xs={4}>
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => this.onCancel()}
                  >
                    Hủy
                  </Button>
                </Grid>
                <Grid item xs={2}></Grid>
                <Grid item xs={4}>
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => this.onUpdate()}
                  >
                    Cập nhật đơn hàng
                  </Button>
                </Grid>
                <Grid item xs={3}></Grid>
              </Grid>
            ) : (
              <Grid container spacing={3} style={{ marginTop: "15px" }}>
                <Grid item xs={4}></Grid>
                <Grid item xs={4}>
                  <Button
                    variant="outlined"
                    fullWidth
                    onClick={() => this.onCancel()}
                  >
                    Hủy
                  </Button>
                </Grid>
                <Grid item xs={4}></Grid>
              </Grid>
            )}
          </Box>
        </Container>
      </div>
    );
  }
}

export default OrderDetailDialog;
