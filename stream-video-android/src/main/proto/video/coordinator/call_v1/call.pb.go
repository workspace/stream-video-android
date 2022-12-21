// Code generated by protoc-gen-go. DO NOT EDIT.
// versions:
// 	protoc-gen-go v1.28.1
// 	protoc        v3.21.5
// source: video/coordinator/call_v1/call.proto

package call_v1

import (
	member_v1 "github.com/GetStream/video-proto/protobuf/video/coordinator/member_v1"
	_ "github.com/envoyproxy/protoc-gen-validate/validate"
	protoreflect "google.golang.org/protobuf/reflect/protoreflect"
	protoimpl "google.golang.org/protobuf/runtime/protoimpl"
	timestamppb "google.golang.org/protobuf/types/known/timestamppb"
	reflect "reflect"
	sync "sync"
)

const (
	// Verify that this generated code is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(20 - protoimpl.MinVersion)
	// Verify that runtime/protoimpl is sufficiently up-to-date.
	_ = protoimpl.EnforceVersion(protoimpl.MaxVersion - 20)
)

type CallType struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// The unique name for the call type.
	Name      string                 `protobuf:"bytes,1,opt,name=name,proto3" json:"name,omitempty"`
	Settings  *CallSettings          `protobuf:"bytes,2,opt,name=settings,proto3" json:"settings,omitempty"`
	CreatedAt *timestamppb.Timestamp `protobuf:"bytes,3,opt,name=created_at,json=createdAt,proto3" json:"created_at,omitempty"`
	UpdatedAt *timestamppb.Timestamp `protobuf:"bytes,4,opt,name=updated_at,json=updatedAt,proto3" json:"updated_at,omitempty"`
}

func (x *CallType) Reset() {
	*x = CallType{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_call_v1_call_proto_msgTypes[0]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *CallType) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*CallType) ProtoMessage() {}

func (x *CallType) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_call_v1_call_proto_msgTypes[0]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use CallType.ProtoReflect.Descriptor instead.
func (*CallType) Descriptor() ([]byte, []int) {
	return file_video_coordinator_call_v1_call_proto_rawDescGZIP(), []int{0}
}

func (x *CallType) GetName() string {
	if x != nil {
		return x.Name
	}
	return ""
}

func (x *CallType) GetSettings() *CallSettings {
	if x != nil {
		return x.Settings
	}
	return nil
}

func (x *CallType) GetCreatedAt() *timestamppb.Timestamp {
	if x != nil {
		return x.CreatedAt
	}
	return nil
}

func (x *CallType) GetUpdatedAt() *timestamppb.Timestamp {
	if x != nil {
		return x.UpdatedAt
	}
	return nil
}

type Call struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// The call type.
	Type string `protobuf:"bytes,1,opt,name=type,proto3" json:"type,omitempty"`
	// The call id.
	Id string `protobuf:"bytes,2,opt,name=id,proto3" json:"id,omitempty"`
	// A concatenation of call type and call id with ":" inbetween
	CallCid string `protobuf:"bytes,3,opt,name=call_cid,json=callCid,proto3" json:"call_cid,omitempty"`
	// The id of the user that created this call.
	CreatedByUserId string `protobuf:"bytes,4,opt,name=created_by_user_id,json=createdByUserId,proto3" json:"created_by_user_id,omitempty"`
	CustomJson      []byte `protobuf:"bytes,5,opt,name=custom_json,json=customJson,proto3" json:"custom_json,omitempty"`
	// Call settings overrides that are set explicitly in this call
	// This set of settings does not include CallType settings
	SettingsOverrides *CallSettings          `protobuf:"bytes,6,opt,name=settings_overrides,json=settingsOverrides,proto3" json:"settings_overrides,omitempty"`
	CreatedAt         *timestamppb.Timestamp `protobuf:"bytes,7,opt,name=created_at,json=createdAt,proto3" json:"created_at,omitempty"`
	UpdatedAt         *timestamppb.Timestamp `protobuf:"bytes,8,opt,name=updated_at,json=updatedAt,proto3" json:"updated_at,omitempty"`
	// If true, the call is currently recording
	RecordingActive bool `protobuf:"varint,9,opt,name=recording_active,json=recordingActive,proto3" json:"recording_active,omitempty"`
	// If true, the call is currently broadcasting
	BroadcastingActive bool `protobuf:"varint,10,opt,name=broadcasting_active,json=broadcastingActive,proto3" json:"broadcasting_active,omitempty"`
}

func (x *Call) Reset() {
	*x = Call{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_call_v1_call_proto_msgTypes[1]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *Call) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*Call) ProtoMessage() {}

func (x *Call) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_call_v1_call_proto_msgTypes[1]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use Call.ProtoReflect.Descriptor instead.
func (*Call) Descriptor() ([]byte, []int) {
	return file_video_coordinator_call_v1_call_proto_rawDescGZIP(), []int{1}
}

func (x *Call) GetType() string {
	if x != nil {
		return x.Type
	}
	return ""
}

func (x *Call) GetId() string {
	if x != nil {
		return x.Id
	}
	return ""
}

func (x *Call) GetCallCid() string {
	if x != nil {
		return x.CallCid
	}
	return ""
}

func (x *Call) GetCreatedByUserId() string {
	if x != nil {
		return x.CreatedByUserId
	}
	return ""
}

func (x *Call) GetCustomJson() []byte {
	if x != nil {
		return x.CustomJson
	}
	return nil
}

func (x *Call) GetSettingsOverrides() *CallSettings {
	if x != nil {
		return x.SettingsOverrides
	}
	return nil
}

func (x *Call) GetCreatedAt() *timestamppb.Timestamp {
	if x != nil {
		return x.CreatedAt
	}
	return nil
}

func (x *Call) GetUpdatedAt() *timestamppb.Timestamp {
	if x != nil {
		return x.UpdatedAt
	}
	return nil
}

func (x *Call) GetRecordingActive() bool {
	if x != nil {
		return x.RecordingActive
	}
	return false
}

func (x *Call) GetBroadcastingActive() bool {
	if x != nil {
		return x.BroadcastingActive
	}
	return false
}

// CallDetails contains call additional details
type CallDetails struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Call settings_overrides merged with CallType settings
	Settings *CallSettings `protobuf:"bytes,1,opt,name=settings,proto3" json:"settings,omitempty"`
	// Ordered list of member user IDs
	MemberUserIds []string `protobuf:"bytes,2,rep,name=member_user_ids,json=memberUserIds,proto3" json:"member_user_ids,omitempty"`
	// Call members map indexed by Member.user_id
	// Cannot have more than 100 members
	Members map[string]*member_v1.Member `protobuf:"bytes,3,rep,name=members,proto3" json:"members,omitempty" protobuf_key:"bytes,1,opt,name=key,proto3" protobuf_val:"bytes,2,opt,name=value,proto3"`
}

func (x *CallDetails) Reset() {
	*x = CallDetails{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_call_v1_call_proto_msgTypes[2]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *CallDetails) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*CallDetails) ProtoMessage() {}

func (x *CallDetails) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_call_v1_call_proto_msgTypes[2]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use CallDetails.ProtoReflect.Descriptor instead.
func (*CallDetails) Descriptor() ([]byte, []int) {
	return file_video_coordinator_call_v1_call_proto_rawDescGZIP(), []int{2}
}

func (x *CallDetails) GetSettings() *CallSettings {
	if x != nil {
		return x.Settings
	}
	return nil
}

func (x *CallDetails) GetMemberUserIds() []string {
	if x != nil {
		return x.MemberUserIds
	}
	return nil
}

func (x *CallDetails) GetMembers() map[string]*member_v1.Member {
	if x != nil {
		return x.Members
	}
	return nil
}

// CallSettings contains all options available to change for a CallType
// Settings can also be set on the call level where they will be merged with call options using `json.Merge`
// To make sure options can be overridden on the call level, all underlying option fields should be optional
type CallSettings struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	Recording    *RecordingSettings    `protobuf:"bytes,1,opt,name=recording,proto3" json:"recording,omitempty"`
	Broadcasting *BroadcastingSettings `protobuf:"bytes,2,opt,name=broadcasting,proto3" json:"broadcasting,omitempty"`
	Geofencing   *GeofencingSettings   `protobuf:"bytes,3,opt,name=geofencing,proto3" json:"geofencing,omitempty"`
}

func (x *CallSettings) Reset() {
	*x = CallSettings{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_call_v1_call_proto_msgTypes[3]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *CallSettings) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*CallSettings) ProtoMessage() {}

func (x *CallSettings) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_call_v1_call_proto_msgTypes[3]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use CallSettings.ProtoReflect.Descriptor instead.
func (*CallSettings) Descriptor() ([]byte, []int) {
	return file_video_coordinator_call_v1_call_proto_rawDescGZIP(), []int{3}
}

func (x *CallSettings) GetRecording() *RecordingSettings {
	if x != nil {
		return x.Recording
	}
	return nil
}

func (x *CallSettings) GetBroadcasting() *BroadcastingSettings {
	if x != nil {
		return x.Broadcasting
	}
	return nil
}

func (x *CallSettings) GetGeofencing() *GeofencingSettings {
	if x != nil {
		return x.Geofencing
	}
	return nil
}

// Contains all settings regarding to call recording
type RecordingSettings struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Whether recording feature is enabled
	// Default: false
	Enabled *bool `protobuf:"varint,1,opt,name=enabled,proto3,oneof" json:"enabled,omitempty"`
}

func (x *RecordingSettings) Reset() {
	*x = RecordingSettings{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_call_v1_call_proto_msgTypes[4]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *RecordingSettings) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*RecordingSettings) ProtoMessage() {}

func (x *RecordingSettings) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_call_v1_call_proto_msgTypes[4]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use RecordingSettings.ProtoReflect.Descriptor instead.
func (*RecordingSettings) Descriptor() ([]byte, []int) {
	return file_video_coordinator_call_v1_call_proto_rawDescGZIP(), []int{4}
}

func (x *RecordingSettings) GetEnabled() bool {
	if x != nil && x.Enabled != nil {
		return *x.Enabled
	}
	return false
}

// Contains all settings regarding to call broadcasting
type BroadcastingSettings struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Whether broadcasting feature is enabled
	// Default: false
	Enabled *bool `protobuf:"varint,1,opt,name=enabled,proto3,oneof" json:"enabled,omitempty"`
}

func (x *BroadcastingSettings) Reset() {
	*x = BroadcastingSettings{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_call_v1_call_proto_msgTypes[5]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *BroadcastingSettings) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*BroadcastingSettings) ProtoMessage() {}

func (x *BroadcastingSettings) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_call_v1_call_proto_msgTypes[5]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use BroadcastingSettings.ProtoReflect.Descriptor instead.
func (*BroadcastingSettings) Descriptor() ([]byte, []int) {
	return file_video_coordinator_call_v1_call_proto_rawDescGZIP(), []int{5}
}

func (x *BroadcastingSettings) GetEnabled() bool {
	if x != nil && x.Enabled != nil {
		return *x.Enabled
	}
	return false
}

// Contains all settings regarding to call geofencing
// Initialization of geofencing enables the feature
type GeofencingSettings struct {
	state         protoimpl.MessageState
	sizeCache     protoimpl.SizeCache
	unknownFields protoimpl.UnknownFields

	// Names of the geofences that are selected
	Names []string `protobuf:"bytes,1,rep,name=names,proto3" json:"names,omitempty"`
}

func (x *GeofencingSettings) Reset() {
	*x = GeofencingSettings{}
	if protoimpl.UnsafeEnabled {
		mi := &file_video_coordinator_call_v1_call_proto_msgTypes[6]
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		ms.StoreMessageInfo(mi)
	}
}

func (x *GeofencingSettings) String() string {
	return protoimpl.X.MessageStringOf(x)
}

func (*GeofencingSettings) ProtoMessage() {}

func (x *GeofencingSettings) ProtoReflect() protoreflect.Message {
	mi := &file_video_coordinator_call_v1_call_proto_msgTypes[6]
	if protoimpl.UnsafeEnabled && x != nil {
		ms := protoimpl.X.MessageStateOf(protoimpl.Pointer(x))
		if ms.LoadMessageInfo() == nil {
			ms.StoreMessageInfo(mi)
		}
		return ms
	}
	return mi.MessageOf(x)
}

// Deprecated: Use GeofencingSettings.ProtoReflect.Descriptor instead.
func (*GeofencingSettings) Descriptor() ([]byte, []int) {
	return file_video_coordinator_call_v1_call_proto_rawDescGZIP(), []int{6}
}

func (x *GeofencingSettings) GetNames() []string {
	if x != nil {
		return x.Names
	}
	return nil
}

var File_video_coordinator_call_v1_call_proto protoreflect.FileDescriptor

var file_video_coordinator_call_v1_call_proto_rawDesc = []byte{
	0x0a, 0x24, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2f, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61,
	0x74, 0x6f, 0x72, 0x2f, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31, 0x2f, 0x63, 0x61, 0x6c, 0x6c,
	0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x12, 0x20, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76,
	0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72,
	0x2e, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31, 0x1a, 0x1f, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65,
	0x2f, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2f, 0x74, 0x69, 0x6d, 0x65, 0x73, 0x74,
	0x61, 0x6d, 0x70, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x1a, 0x17, 0x76, 0x61, 0x6c, 0x69, 0x64,
	0x61, 0x74, 0x65, 0x2f, 0x76, 0x61, 0x6c, 0x69, 0x64, 0x61, 0x74, 0x65, 0x2e, 0x70, 0x72, 0x6f,
	0x74, 0x6f, 0x1a, 0x28, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2f, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69,
	0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2f, 0x6d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x5f, 0x76, 0x31, 0x2f,
	0x6d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x22, 0xe0, 0x01, 0x0a,
	0x08, 0x43, 0x61, 0x6c, 0x6c, 0x54, 0x79, 0x70, 0x65, 0x12, 0x12, 0x0a, 0x04, 0x6e, 0x61, 0x6d,
	0x65, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x04, 0x6e, 0x61, 0x6d, 0x65, 0x12, 0x4a, 0x0a,
	0x08, 0x73, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0b, 0x32,
	0x2e, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63,
	0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x63, 0x61, 0x6c, 0x6c, 0x5f,
	0x76, 0x31, 0x2e, 0x43, 0x61, 0x6c, 0x6c, 0x53, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x52,
	0x08, 0x73, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x12, 0x39, 0x0a, 0x0a, 0x63, 0x72, 0x65,
	0x61, 0x74, 0x65, 0x64, 0x5f, 0x61, 0x74, 0x18, 0x03, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x1a, 0x2e,
	0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e,
	0x54, 0x69, 0x6d, 0x65, 0x73, 0x74, 0x61, 0x6d, 0x70, 0x52, 0x09, 0x63, 0x72, 0x65, 0x61, 0x74,
	0x65, 0x64, 0x41, 0x74, 0x12, 0x39, 0x0a, 0x0a, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65, 0x64, 0x5f,
	0x61, 0x74, 0x18, 0x04, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x1a, 0x2e, 0x67, 0x6f, 0x6f, 0x67, 0x6c,
	0x65, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e, 0x54, 0x69, 0x6d, 0x65, 0x73,
	0x74, 0x61, 0x6d, 0x70, 0x52, 0x09, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65, 0x64, 0x41, 0x74, 0x22,
	0xdf, 0x03, 0x0a, 0x04, 0x43, 0x61, 0x6c, 0x6c, 0x12, 0x1b, 0x0a, 0x04, 0x74, 0x79, 0x70, 0x65,
	0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x42, 0x07, 0xfa, 0x42, 0x04, 0x72, 0x02, 0x10, 0x01, 0x52,
	0x04, 0x74, 0x79, 0x70, 0x65, 0x12, 0x17, 0x0a, 0x02, 0x69, 0x64, 0x18, 0x02, 0x20, 0x01, 0x28,
	0x09, 0x42, 0x07, 0xfa, 0x42, 0x04, 0x72, 0x02, 0x10, 0x01, 0x52, 0x02, 0x69, 0x64, 0x12, 0x19,
	0x0a, 0x08, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x63, 0x69, 0x64, 0x18, 0x03, 0x20, 0x01, 0x28, 0x09,
	0x52, 0x07, 0x63, 0x61, 0x6c, 0x6c, 0x43, 0x69, 0x64, 0x12, 0x34, 0x0a, 0x12, 0x63, 0x72, 0x65,
	0x61, 0x74, 0x65, 0x64, 0x5f, 0x62, 0x79, 0x5f, 0x75, 0x73, 0x65, 0x72, 0x5f, 0x69, 0x64, 0x18,
	0x04, 0x20, 0x01, 0x28, 0x09, 0x42, 0x07, 0xfa, 0x42, 0x04, 0x72, 0x02, 0x10, 0x01, 0x52, 0x0f,
	0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x64, 0x42, 0x79, 0x55, 0x73, 0x65, 0x72, 0x49, 0x64, 0x12,
	0x1f, 0x0a, 0x0b, 0x63, 0x75, 0x73, 0x74, 0x6f, 0x6d, 0x5f, 0x6a, 0x73, 0x6f, 0x6e, 0x18, 0x05,
	0x20, 0x01, 0x28, 0x0c, 0x52, 0x0a, 0x63, 0x75, 0x73, 0x74, 0x6f, 0x6d, 0x4a, 0x73, 0x6f, 0x6e,
	0x12, 0x5d, 0x0a, 0x12, 0x73, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x5f, 0x6f, 0x76, 0x65,
	0x72, 0x72, 0x69, 0x64, 0x65, 0x73, 0x18, 0x06, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x2e, 0x2e, 0x73,
	0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72,
	0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31, 0x2e,
	0x43, 0x61, 0x6c, 0x6c, 0x53, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x52, 0x11, 0x73, 0x65,
	0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x4f, 0x76, 0x65, 0x72, 0x72, 0x69, 0x64, 0x65, 0x73, 0x12,
	0x39, 0x0a, 0x0a, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x64, 0x5f, 0x61, 0x74, 0x18, 0x07, 0x20,
	0x01, 0x28, 0x0b, 0x32, 0x1a, 0x2e, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, 0x2e, 0x70, 0x72, 0x6f,
	0x74, 0x6f, 0x62, 0x75, 0x66, 0x2e, 0x54, 0x69, 0x6d, 0x65, 0x73, 0x74, 0x61, 0x6d, 0x70, 0x52,
	0x09, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x64, 0x41, 0x74, 0x12, 0x39, 0x0a, 0x0a, 0x75, 0x70,
	0x64, 0x61, 0x74, 0x65, 0x64, 0x5f, 0x61, 0x74, 0x18, 0x08, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x1a,
	0x2e, 0x67, 0x6f, 0x6f, 0x67, 0x6c, 0x65, 0x2e, 0x70, 0x72, 0x6f, 0x74, 0x6f, 0x62, 0x75, 0x66,
	0x2e, 0x54, 0x69, 0x6d, 0x65, 0x73, 0x74, 0x61, 0x6d, 0x70, 0x52, 0x09, 0x75, 0x70, 0x64, 0x61,
	0x74, 0x65, 0x64, 0x41, 0x74, 0x12, 0x29, 0x0a, 0x10, 0x72, 0x65, 0x63, 0x6f, 0x72, 0x64, 0x69,
	0x6e, 0x67, 0x5f, 0x61, 0x63, 0x74, 0x69, 0x76, 0x65, 0x18, 0x09, 0x20, 0x01, 0x28, 0x08, 0x52,
	0x0f, 0x72, 0x65, 0x63, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x67, 0x41, 0x63, 0x74, 0x69, 0x76, 0x65,
	0x12, 0x2f, 0x0a, 0x13, 0x62, 0x72, 0x6f, 0x61, 0x64, 0x63, 0x61, 0x73, 0x74, 0x69, 0x6e, 0x67,
	0x5f, 0x61, 0x63, 0x74, 0x69, 0x76, 0x65, 0x18, 0x0a, 0x20, 0x01, 0x28, 0x08, 0x52, 0x12, 0x62,
	0x72, 0x6f, 0x61, 0x64, 0x63, 0x61, 0x73, 0x74, 0x69, 0x6e, 0x67, 0x41, 0x63, 0x74, 0x69, 0x76,
	0x65, 0x22, 0xbf, 0x02, 0x0a, 0x0b, 0x43, 0x61, 0x6c, 0x6c, 0x44, 0x65, 0x74, 0x61, 0x69, 0x6c,
	0x73, 0x12, 0x4a, 0x0a, 0x08, 0x73, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x18, 0x01, 0x20,
	0x01, 0x28, 0x0b, 0x32, 0x2e, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64,
	0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x63,
	0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31, 0x2e, 0x43, 0x61, 0x6c, 0x6c, 0x53, 0x65, 0x74, 0x74, 0x69,
	0x6e, 0x67, 0x73, 0x52, 0x08, 0x73, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x12, 0x26, 0x0a,
	0x0f, 0x6d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x5f, 0x75, 0x73, 0x65, 0x72, 0x5f, 0x69, 0x64, 0x73,
	0x18, 0x02, 0x20, 0x03, 0x28, 0x09, 0x52, 0x0d, 0x6d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x55, 0x73,
	0x65, 0x72, 0x49, 0x64, 0x73, 0x12, 0x54, 0x0a, 0x07, 0x6d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x73,
	0x18, 0x03, 0x20, 0x03, 0x28, 0x0b, 0x32, 0x3a, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e,
	0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f,
	0x72, 0x2e, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31, 0x2e, 0x43, 0x61, 0x6c, 0x6c, 0x44, 0x65,
	0x74, 0x61, 0x69, 0x6c, 0x73, 0x2e, 0x4d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x73, 0x45, 0x6e, 0x74,
	0x72, 0x79, 0x52, 0x07, 0x6d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x73, 0x1a, 0x66, 0x0a, 0x0c, 0x4d,
	0x65, 0x6d, 0x62, 0x65, 0x72, 0x73, 0x45, 0x6e, 0x74, 0x72, 0x79, 0x12, 0x10, 0x0a, 0x03, 0x6b,
	0x65, 0x79, 0x18, 0x01, 0x20, 0x01, 0x28, 0x09, 0x52, 0x03, 0x6b, 0x65, 0x79, 0x12, 0x40, 0x0a,
	0x05, 0x76, 0x61, 0x6c, 0x75, 0x65, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x2a, 0x2e, 0x73,
	0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72,
	0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x6d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x5f, 0x76,
	0x31, 0x2e, 0x4d, 0x65, 0x6d, 0x62, 0x65, 0x72, 0x52, 0x05, 0x76, 0x61, 0x6c, 0x75, 0x65, 0x3a,
	0x02, 0x38, 0x01, 0x22, 0x93, 0x02, 0x0a, 0x0c, 0x43, 0x61, 0x6c, 0x6c, 0x53, 0x65, 0x74, 0x74,
	0x69, 0x6e, 0x67, 0x73, 0x12, 0x51, 0x0a, 0x09, 0x72, 0x65, 0x63, 0x6f, 0x72, 0x64, 0x69, 0x6e,
	0x67, 0x18, 0x01, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x33, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d,
	0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74,
	0x6f, 0x72, 0x2e, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31, 0x2e, 0x52, 0x65, 0x63, 0x6f, 0x72,
	0x64, 0x69, 0x6e, 0x67, 0x53, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x52, 0x09, 0x72, 0x65,
	0x63, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x67, 0x12, 0x5a, 0x0a, 0x0c, 0x62, 0x72, 0x6f, 0x61, 0x64,
	0x63, 0x61, 0x73, 0x74, 0x69, 0x6e, 0x67, 0x18, 0x02, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x36, 0x2e,
	0x73, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f,
	0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x2e, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31,
	0x2e, 0x42, 0x72, 0x6f, 0x61, 0x64, 0x63, 0x61, 0x73, 0x74, 0x69, 0x6e, 0x67, 0x53, 0x65, 0x74,
	0x74, 0x69, 0x6e, 0x67, 0x73, 0x52, 0x0c, 0x62, 0x72, 0x6f, 0x61, 0x64, 0x63, 0x61, 0x73, 0x74,
	0x69, 0x6e, 0x67, 0x12, 0x54, 0x0a, 0x0a, 0x67, 0x65, 0x6f, 0x66, 0x65, 0x6e, 0x63, 0x69, 0x6e,
	0x67, 0x18, 0x03, 0x20, 0x01, 0x28, 0x0b, 0x32, 0x34, 0x2e, 0x73, 0x74, 0x72, 0x65, 0x61, 0x6d,
	0x2e, 0x76, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x63, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74,
	0x6f, 0x72, 0x2e, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31, 0x2e, 0x47, 0x65, 0x6f, 0x66, 0x65,
	0x6e, 0x63, 0x69, 0x6e, 0x67, 0x53, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x52, 0x0a, 0x67,
	0x65, 0x6f, 0x66, 0x65, 0x6e, 0x63, 0x69, 0x6e, 0x67, 0x22, 0x3e, 0x0a, 0x11, 0x52, 0x65, 0x63,
	0x6f, 0x72, 0x64, 0x69, 0x6e, 0x67, 0x53, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67, 0x73, 0x12, 0x1d,
	0x0a, 0x07, 0x65, 0x6e, 0x61, 0x62, 0x6c, 0x65, 0x64, 0x18, 0x01, 0x20, 0x01, 0x28, 0x08, 0x48,
	0x00, 0x52, 0x07, 0x65, 0x6e, 0x61, 0x62, 0x6c, 0x65, 0x64, 0x88, 0x01, 0x01, 0x42, 0x0a, 0x0a,
	0x08, 0x5f, 0x65, 0x6e, 0x61, 0x62, 0x6c, 0x65, 0x64, 0x22, 0x41, 0x0a, 0x14, 0x42, 0x72, 0x6f,
	0x61, 0x64, 0x63, 0x61, 0x73, 0x74, 0x69, 0x6e, 0x67, 0x53, 0x65, 0x74, 0x74, 0x69, 0x6e, 0x67,
	0x73, 0x12, 0x1d, 0x0a, 0x07, 0x65, 0x6e, 0x61, 0x62, 0x6c, 0x65, 0x64, 0x18, 0x01, 0x20, 0x01,
	0x28, 0x08, 0x48, 0x00, 0x52, 0x07, 0x65, 0x6e, 0x61, 0x62, 0x6c, 0x65, 0x64, 0x88, 0x01, 0x01,
	0x42, 0x0a, 0x0a, 0x08, 0x5f, 0x65, 0x6e, 0x61, 0x62, 0x6c, 0x65, 0x64, 0x22, 0x2a, 0x0a, 0x12,
	0x47, 0x65, 0x6f, 0x66, 0x65, 0x6e, 0x63, 0x69, 0x6e, 0x67, 0x53, 0x65, 0x74, 0x74, 0x69, 0x6e,
	0x67, 0x73, 0x12, 0x14, 0x0a, 0x05, 0x6e, 0x61, 0x6d, 0x65, 0x73, 0x18, 0x01, 0x20, 0x03, 0x28,
	0x09, 0x52, 0x05, 0x6e, 0x61, 0x6d, 0x65, 0x73, 0x42, 0x31, 0x42, 0x06, 0x43, 0x61, 0x6c, 0x6c,
	0x56, 0x31, 0x50, 0x01, 0x5a, 0x07, 0x63, 0x61, 0x6c, 0x6c, 0x5f, 0x76, 0x31, 0xaa, 0x02, 0x1b,
	0x53, 0x74, 0x72, 0x65, 0x61, 0x6d, 0x2e, 0x56, 0x69, 0x64, 0x65, 0x6f, 0x2e, 0x76, 0x31, 0x2e,
	0x43, 0x6f, 0x6f, 0x72, 0x64, 0x69, 0x6e, 0x61, 0x74, 0x6f, 0x72, 0x62, 0x06, 0x70, 0x72, 0x6f,
	0x74, 0x6f, 0x33,
}

var (
	file_video_coordinator_call_v1_call_proto_rawDescOnce sync.Once
	file_video_coordinator_call_v1_call_proto_rawDescData = file_video_coordinator_call_v1_call_proto_rawDesc
)

func file_video_coordinator_call_v1_call_proto_rawDescGZIP() []byte {
	file_video_coordinator_call_v1_call_proto_rawDescOnce.Do(func() {
		file_video_coordinator_call_v1_call_proto_rawDescData = protoimpl.X.CompressGZIP(file_video_coordinator_call_v1_call_proto_rawDescData)
	})
	return file_video_coordinator_call_v1_call_proto_rawDescData
}

var file_video_coordinator_call_v1_call_proto_msgTypes = make([]protoimpl.MessageInfo, 8)
var file_video_coordinator_call_v1_call_proto_goTypes = []interface{}{
	(*CallType)(nil),              // 0: stream.video.coordinator.call_v1.CallType
	(*Call)(nil),                  // 1: stream.video.coordinator.call_v1.Call
	(*CallDetails)(nil),           // 2: stream.video.coordinator.call_v1.CallDetails
	(*CallSettings)(nil),          // 3: stream.video.coordinator.call_v1.CallSettings
	(*RecordingSettings)(nil),     // 4: stream.video.coordinator.call_v1.RecordingSettings
	(*BroadcastingSettings)(nil),  // 5: stream.video.coordinator.call_v1.BroadcastingSettings
	(*GeofencingSettings)(nil),    // 6: stream.video.coordinator.call_v1.GeofencingSettings
	nil,                           // 7: stream.video.coordinator.call_v1.CallDetails.MembersEntry
	(*timestamppb.Timestamp)(nil), // 8: google.protobuf.Timestamp
	(*member_v1.Member)(nil),      // 9: stream.video.coordinator.member_v1.Member
}
var file_video_coordinator_call_v1_call_proto_depIdxs = []int32{
	3,  // 0: stream.video.coordinator.call_v1.CallType.settings:type_name -> stream.video.coordinator.call_v1.CallSettings
	8,  // 1: stream.video.coordinator.call_v1.CallType.created_at:type_name -> google.protobuf.Timestamp
	8,  // 2: stream.video.coordinator.call_v1.CallType.updated_at:type_name -> google.protobuf.Timestamp
	3,  // 3: stream.video.coordinator.call_v1.Call.settings_overrides:type_name -> stream.video.coordinator.call_v1.CallSettings
	8,  // 4: stream.video.coordinator.call_v1.Call.created_at:type_name -> google.protobuf.Timestamp
	8,  // 5: stream.video.coordinator.call_v1.Call.updated_at:type_name -> google.protobuf.Timestamp
	3,  // 6: stream.video.coordinator.call_v1.CallDetails.settings:type_name -> stream.video.coordinator.call_v1.CallSettings
	7,  // 7: stream.video.coordinator.call_v1.CallDetails.members:type_name -> stream.video.coordinator.call_v1.CallDetails.MembersEntry
	4,  // 8: stream.video.coordinator.call_v1.CallSettings.recording:type_name -> stream.video.coordinator.call_v1.RecordingSettings
	5,  // 9: stream.video.coordinator.call_v1.CallSettings.broadcasting:type_name -> stream.video.coordinator.call_v1.BroadcastingSettings
	6,  // 10: stream.video.coordinator.call_v1.CallSettings.geofencing:type_name -> stream.video.coordinator.call_v1.GeofencingSettings
	9,  // 11: stream.video.coordinator.call_v1.CallDetails.MembersEntry.value:type_name -> stream.video.coordinator.member_v1.Member
	12, // [12:12] is the sub-list for method output_type
	12, // [12:12] is the sub-list for method input_type
	12, // [12:12] is the sub-list for extension type_name
	12, // [12:12] is the sub-list for extension extendee
	0,  // [0:12] is the sub-list for field type_name
}

func init() { file_video_coordinator_call_v1_call_proto_init() }
func file_video_coordinator_call_v1_call_proto_init() {
	if File_video_coordinator_call_v1_call_proto != nil {
		return
	}
	if !protoimpl.UnsafeEnabled {
		file_video_coordinator_call_v1_call_proto_msgTypes[0].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*CallType); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_call_v1_call_proto_msgTypes[1].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*Call); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_call_v1_call_proto_msgTypes[2].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*CallDetails); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_call_v1_call_proto_msgTypes[3].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*CallSettings); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_call_v1_call_proto_msgTypes[4].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*RecordingSettings); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_call_v1_call_proto_msgTypes[5].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*BroadcastingSettings); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
		file_video_coordinator_call_v1_call_proto_msgTypes[6].Exporter = func(v interface{}, i int) interface{} {
			switch v := v.(*GeofencingSettings); i {
			case 0:
				return &v.state
			case 1:
				return &v.sizeCache
			case 2:
				return &v.unknownFields
			default:
				return nil
			}
		}
	}
	file_video_coordinator_call_v1_call_proto_msgTypes[4].OneofWrappers = []interface{}{}
	file_video_coordinator_call_v1_call_proto_msgTypes[5].OneofWrappers = []interface{}{}
	type x struct{}
	out := protoimpl.TypeBuilder{
		File: protoimpl.DescBuilder{
			GoPackagePath: reflect.TypeOf(x{}).PkgPath(),
			RawDescriptor: file_video_coordinator_call_v1_call_proto_rawDesc,
			NumEnums:      0,
			NumMessages:   8,
			NumExtensions: 0,
			NumServices:   0,
		},
		GoTypes:           file_video_coordinator_call_v1_call_proto_goTypes,
		DependencyIndexes: file_video_coordinator_call_v1_call_proto_depIdxs,
		MessageInfos:      file_video_coordinator_call_v1_call_proto_msgTypes,
	}.Build()
	File_video_coordinator_call_v1_call_proto = out.File
	file_video_coordinator_call_v1_call_proto_rawDesc = nil
	file_video_coordinator_call_v1_call_proto_goTypes = nil
	file_video_coordinator_call_v1_call_proto_depIdxs = nil
}
