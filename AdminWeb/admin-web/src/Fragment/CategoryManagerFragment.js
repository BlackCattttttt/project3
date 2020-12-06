import React, { Component } from "react";
import { withStyles, makeStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import { firestore } from "../firebase";
import { Container, Box, Typography } from "@material-ui/core";
import { Edit } from "@material-ui/icons";
import { blue } from "@material-ui/core/colors";
import { Button, Grid, TextField } from "@material-ui/core";
import { Alert } from "@material-ui/lab";

const StyledTableCell = withStyles((theme) => ({
  head: {
    backgroundColor: theme.palette.common.black,
    color: theme.palette.common.white,
  },
  body: {
    fontSize: 14,
  },
}))(TableCell);

const StyledTableRow = withStyles((theme) => ({
  root: {
    "&:nth-of-type(odd)": {
      backgroundColor: theme.palette.action.hover,
    },
  },
}))(TableRow);

function createData(
  id,
  name,
  price,
  surchage,
  type,
  amount,
  percent,
  ebayId,
  docId
) {
  return { id, name, price, surchage, type, amount, percent, ebayId, docId };
}

const useStyles = makeStyles({
  table: {
    minWidth: 700,
  },
});
const surcharges = [
  {
    value: "0",
    label: "Không có phụ phí",
  },
  {
    value: "1",
    label: "Phụ thu theo giá",
  },
  {
    value: "2",
    label: "Phụ thu theo số lượng",
  },
  {
    value: "3",
    label: "Phụ thu theo tổng giá",
  },
];

let visibility1 = false;
let visibility2 = false;
let visibility3 = false;

class CategoryManagerFragment extends Component {
  constructor(props) {
    super(props);

    this.state = {
      classes: {},
      rows: [],
      showEditDialog: false,
      success: false,
      error: false,
      surcharges: "1",
      name: "",
      price: "",
      type: "",
      amount: "",
      percent: "",
      id: "",
      docId: "",
    };
  }
  handleChange1 = (event) => {
    this.setState({
      surcharges: event.target.value,
      [event.target.name]: event.target.value,
    });
    if (event.target.value === "1") {
      visibility1 = true;
      visibility2 = false;
      visibility3 = false;
    } else if (event.target.value === "2") {
      visibility1 = false;
      visibility2 = true;
      visibility3 = false;
    } else if (event.target.value === "3") {
      visibility1 = false;
      visibility2 = false;
      visibility3 = true;
    } else {
      visibility1 = false;
      visibility2 = false;
      visibility3 = false;
    }
  };
  handleChange = (e) => {
    this.setState({
      [e.target.name]: e.target.value,
    });
  };
  componentDidMount() {
    this.state.classes = useStyles;
    this.getCategoryDatabase();
  }
  getCategoryDatabase() {
    firestore
      .collection("Category")
      .get()
      .then((querySnapshot) => {
        let categories = [];
        let index = 0;
        querySnapshot.forEach(function (doc) {
          index++;
          let category = createData(
            index,
            doc.get("name"),
            doc.get("price"),
            doc.get("surcharge"),
            doc.get("surchargeType"),
            doc.get("priceSurcharge"),
            doc.get("percentSurcharge"),
            doc.get("id"),
            doc.id
          );
          categories.push(category);
        });
        this.setState({
          rows: categories,
        });
      });
  }

  edit = (key) => {
    this.setState({
      showEditDialog: true,
      name: this.state.rows[key - 1].name,
      price: this.state.rows[key - 1].price,
      type: this.state.rows[key - 1].type,
      amount: this.state.rows[key - 1].amount,
      percent: this.state.rows[key - 1].percent,
      id: this.state.rows[key - 1].ebayId,
      docId: this.state.rows[key - 1].docId,
    });
  };

  onCancel = () => {
    this.setState({
      showEditDialog: false,
    });
  };

  onUpdate = () => {
    this.setState({
      showEditDialog: false,
    });
    if (this.state.price === "") {
      this.state.price = "0";
    }
    if (this.state.amount === "") {
      this.state.amount = "0";
    }
    if (this.state.percent === "") {
      this.state.percent = "0";
    }
    let sur = "";
    if (this.state.type === "0") {
      sur = "";
      this.state.percent = "0";
      this.state.amount = "0";
    } else if (this.state.type === "1") {
      sur = ">$" + this.state.amount + ":phụ thu " + this.state.percent + "%";
    } else if (this.state.type === "2") {
      sur = "$" + this.state.amount + "/cái";
      this.state.percent = "0";
    } else if (this.state.type === "3") {
      sur = this.state.percent + "%";
      this.state.amount = "0";
    }
    let data = {
      id: this.state.id,
      name: this.state.name,
      percentSurcharge: parseInt(this.state.percent),
      price: parseInt(this.state.price),
      priceSurcharge: parseInt(this.state.amount),
      surchargeType: parseInt(this.state.type),
      surcharge: sur,
    };
    firestore
      .collection("Category")
      .doc(this.state.docId)
      .update(data)
      .then(() => {
        console.log("Document successfully updated!");
        this.getCategoryDatabase();
        this.setState({
          success: true,
          error: false,
        });
      })
      .catch(function (error) {
        // The document probably doesn't exist.
        console.error("Error updating document: ", error);
        this.setState({
          success: false,
          error: true,
        });
      });
  };
  render() {
    return (
      <div>
        {this.state.success ? (
          <Alert severity="success">Chỉnh sửa thành công</Alert>
        ) : null}
        {this.state.error ? (
          <Alert severity="error">Chỉnh sửa thất bại</Alert>
        ) : null}
        <br />
        <Container maxWidth="lg" minWidth="md" fixed>
          <TableContainer component={Paper}>
            <Table
              className={this.state.classes.table}
              aria-label="customized table"
            >
              <TableHead>
                <TableRow>
                  <StyledTableCell width="100">STT</StyledTableCell>
                  <StyledTableCell align="left" width="600">
                    Loại mặt hàng
                  </StyledTableCell>
                  <StyledTableCell align="left" width="300">
                    Giá vận chuyển(kg)
                  </StyledTableCell>
                  <StyledTableCell align="left" width="300">
                    Phụ thu
                  </StyledTableCell>
                  <StyledTableCell width="200" align="center">
                    Sửa
                  </StyledTableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {this.state.rows.map((row) => (
                  <StyledTableRow key={row.id}>
                    <StyledTableCell width="100">{row.id}</StyledTableCell>
                    <StyledTableCell align="left" width="600">
                      {row.name}
                    </StyledTableCell>
                    <StyledTableCell align="left" width="300">
                      ${row.price}/kg
                    </StyledTableCell>
                    <StyledTableCell align="left" width="300">
                      {row.surchage}
                    </StyledTableCell>
                    <StyledTableCell width="200" align="center">
                      <Edit fontSize="small" />
                      <span
                        style={{ fontSize: 14, color: blue, margin: 2 }}
                        onClick={() => this.edit(row.id)}
                      >
                        <u>Sửa</u>
                      </span>
                    </StyledTableCell>
                  </StyledTableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Container>
        {this.state.showEditDialog ? (
          <div
            style={{
              width: "100vw",
              height: "100vh",
              backgroundColor: "rgb(0, 0, 0, 0.4)",
              zIndex: "100",
              position: "absolute",
              top: 0,
              left: 0,
            }}
          >
            <Container maxWidth="md">
              <Box
                bgcolor="white"
                boxShadow="2"
                borderRadius="15px"
                p="24px"
                mt="50px"
                marginTop="200px"
                marginLeft="150px"
              >
                <Typography variant="h5" color="textprimary">
                  Chỉnh sửa loại mặt hàng
                </Typography>
                <Typography
                  variant="button"
                  display="block"
                  gutterBottom
                  style={{ textAlign: "left", marginTop: 10 }}
                >
                  Loại mặt hàng
                </Typography>
                <TextField
                  label="Loại mặt hàng"
                  id="outlined-size-small"
                  name="name"
                  variant="outlined"
                  fullWidth
                  onChange={this.handleChange}
                  size="small"
                  disabled
                  defaultValue={this.state.name}
                />
                <Grid container spacing={3}>
                  <Grid item xs={6}>
                    <Typography
                      variant="button"
                      display="block"
                      gutterBottom
                      style={{ textAlign: "left", marginTop: 10 }}
                    >
                      Phí vận chuyển(/1 kg)
                    </Typography>
                    <TextField
                      label="Required"
                      id="standard-number"
                      type="number"
                      name="price"
                      onChange={this.handleChange}
                      fullWidth
                      required
                      variant="outlined"
                      size="small"
                      InputLabelProps={{
                        shrink: true,
                      }}
                      defaultValue={this.state.price}
                    />
                  </Grid>
                  <Grid item xs={6}>
                    <Typography
                      variant="button"
                      display="block"
                      gutterBottom
                      style={{ textAlign: "left", marginTop: 10 }}
                    >
                      Loại phụ thu
                    </Typography>
                    <TextField
                      id="standard-select-currency-native"
                      select
                      name="type"
                      value={this.state.surcharge}
                      fullWidth
                      onChange={this.handleChange1}
                      SelectProps={{
                        native: true,
                      }}
                      size="medium"
                      defaultValue={this.state.type}
                    >
                      {surcharges.map((option) => (
                        <option key={option.value} value={option.value}>
                          {option.label}
                        </option>
                      ))}
                    </TextField>
                  </Grid>
                </Grid>
                {visibility1 ? (
                  <Grid container spacing={3}>
                    <Grid item xs={6}>
                      <Typography
                        variant="button"
                        display="block"
                        gutterBottom
                        style={{ textAlign: "left", marginTop: 10 }}
                      >
                        Giá phụ thu(lớn hơn thì phụ thu)
                      </Typography>
                      <TextField
                        label="Required"
                        id="standard-number"
                        type="number"
                        name="amount"
                        fullWidth
                        onChange={this.handleChange}
                        required
                        variant="outlined"
                        size="small"
                        InputLabelProps={{
                          shrink: true,
                        }}
                        defaultValue={this.state.amount}
                      />
                    </Grid>
                    <Grid item xs={6}>
                      <Typography
                        variant="button"
                        display="block"
                        gutterBottom
                        style={{ textAlign: "left", marginTop: 10 }}
                      >
                        Phần trăm phụ thu(%)
                      </Typography>
                      <TextField
                        label="Required"
                        id="standard-number"
                        type="number"
                        name="percent"
                        fullWidth
                        onChange={this.handleChange}
                        required
                        variant="outlined"
                        size="small"
                        InputLabelProps={{
                          shrink: true,
                        }}
                        defaultValue={this.state.percent}
                      />
                    </Grid>
                  </Grid>
                ) : null}
                {visibility2 ? (
                  <Grid container spacing={3}>
                    <Grid item xs={6}>
                      <Typography
                        variant="button"
                        display="block"
                        gutterBottom
                        style={{ textAlign: "left", marginTop: 10 }}
                      >
                        Giá phụ thu(/1 cái)
                      </Typography>
                      <TextField
                        label="Required"
                        id="standard-number"
                        type="number"
                        name="amount"
                        fullWidth
                        onChange={this.handleChange}
                        required
                        variant="outlined"
                        size="small"
                        InputLabelProps={{
                          shrink: true,
                        }}
                        defaultValue={this.state.amount}
                      />
                    </Grid>
                  </Grid>
                ) : null}
                {visibility3 ? (
                  <Grid container spacing={3}>
                    <Grid item xs={6}>
                      <Typography
                        variant="button"
                        display="block"
                        gutterBottom
                        style={{ textAlign: "left", marginTop: 10 }}
                      >
                        Phần trăm phụ thu(%)
                      </Typography>
                      <TextField
                        label="Required"
                        id="standard-number"
                        type="number"
                        name="percent"
                        fullWidth
                        onChange={this.handleChange}
                        required
                        variant="outlined"
                        size="small"
                        InputLabelProps={{
                          shrink: true,
                        }}
                        defaultValue={this.state.percent}
                      />
                    </Grid>
                  </Grid>
                ) : null}
                <Grid container spacing={3}>
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
                      Cập nhật thông tin
                    </Button>
                  </Grid>
                  <Grid item xs={3}></Grid>
                </Grid>
              </Box>
            </Container>
          </div>
        ) : (
          <div></div>
        )}
      </div>
    );
  }
}

export default CategoryManagerFragment;
