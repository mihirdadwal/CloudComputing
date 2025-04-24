# Variables for main packer file
variable "ami_prefix" {
  type        = string
  description = "Prefix to use for the name of the resulting AMI"
}
variable "region" {
  type        = string
  description = "AWS region where the AMI will be built"
}
variable "vpc_id" {
  type        = string
  description = "ID of the VPC to launch the build instance into"
}
variable "subnet_id" {
  type        = string
  description = "ID of the subnet to launch the build instance into"
}
variable "ami_owner" {
  type        = string
  description = "Owner ID or alias of the source AMI"
}
variable "instance_type" {
  type        = string
  description = "EC2 instance type to use for building the AMI"
}
variable "ssh_username" {
  type        = string
  description = "Username to use for SSH access to the build instance"
}
variable "volume_size" {
  type        = number
  description = "Size of the root volume in GB"
}
variable "volume_type" {
  type        = string
  description = "Type of EBS volume to use for the root device"
}
variable "ami_users" {
  type        = string
  description = "List of AWS account IDs that can access the resulting AMI"
}

variable "virtualization_type" {
  default = "hvm"
}

variable "ami_name_filter" {
  default = "ubuntu/images/*ubuntu-noble-24.04-amd64-server-*"
}

variable "root_device_type" {
  default = "ebs"
}

variable "device_name" {
  default = "/dev/sda1"
}

variable "delete_on_termination" {
  default = true
}

variable "tags" {
  type = map(string)
  default = {
    Name = "CSYE6225 AMI"
  }
}

# Variables for build Packer file
variable "build_name" {
  default = "packer"
}

variable "source_ami" {
  default = "source.amazon-ebs.ubuntu"
}

variable "update_script" {
  default = "./packer/update.sh"
}

variable "app_prereq_script" {
  default = "./packer/appPreReqSetup.sh"
}

variable "jar_source" {
  default = "/home/runner/work/webapp/webapp/target/webdev-1.0.0.jar"
}

variable "config_source" {
  default = "/tmp/prodwebappconfig.properties"
}

variable "service_file_source" {
  default = "./packer/csye6225.service"
}

variable "app_dir_setup_script" {
  default = "./packer/appDirSetup.sh"
}

variable "tmp_destination" {
  default = "/tmp/"
}

variable "ami_regions" {
  type        = string
  description = "List of US regions to copy the AMI to"
}

variable "disable_stop_instance" {
  type        = bool
  description = "Disable stop instance"
  default     = false
}