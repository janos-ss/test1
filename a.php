<?php
//error_reporting(E_ALL);
error_reporting(0);

$array = array(
       "a",
       "b",
  6 => "c",
       "d",
);

for($i = 0; $i < count($array); $i++) {
  echo $array[$i].",";
}

?>
