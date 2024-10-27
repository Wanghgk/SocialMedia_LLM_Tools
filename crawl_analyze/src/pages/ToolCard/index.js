import {Box, Button, Card, CardActions, CardContent, CardMedia, Typography} from "@mui/material";
import {Link, NavLink} from "react-router-dom";
import Style from "./ToolCard.module.css";

function ToolCard({image, name, description, }) {

  return (
      <Card sx={{ maxWidth: 345 }} style={{background: "linear-gradient(127.09deg, rgb(142 145 160 / 94%) 19.41%, rgba(36, 38, 51, 0.49) 76.65%)"}}>
          <CardMedia
              component="img"
              alt="green iguana"
              height="140"
              image={image}
          />
          <CardContent>
              <Typography gutterBottom variant="h5" color={"#91ade5"} component="div">
                  {name}
              </Typography>
              <Typography variant="body2" color={"grey"} sx={{ }}>
                  {description}
              </Typography>
          </CardContent>
          <CardActions>
              <Button size="small">Share</Button>
              <NavLink component={Link} to="/BinaryShow"><Button size="small">Learn More</Button></NavLink>

          </CardActions>
      </Card>
  )
}

export default ToolCard;